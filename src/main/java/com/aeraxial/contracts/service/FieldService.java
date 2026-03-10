package com.aeraxial.contracts.service;

import com.aeraxial.contracts.domain.*;
import com.aeraxial.contracts.dto.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class FieldService {

    public List<ContractFieldValue> listValues(long clientId, long versionId) {
        // Version must belong to client
        ContractVersion v = ContractVersion.findById(versionId);
        if (v == null || !v.clientId.equals(clientId)) {
            throw new WebApplicationException("Version not found", Response.Status.NOT_FOUND);
        }
        return ContractFieldValue.list("clientId = ?1 and contractVersionId = ?2", clientId, versionId);
    }

    @Transactional
    public List<ContractFieldValue> bulkUpsert(long clientId, String actor, long versionId, BulkUpsertFieldValuesRequest req) {
        ContractVersion v = ContractVersion.findById(versionId);
        if (v == null || !v.clientId.equals(clientId)) {
            throw new WebApplicationException("Version not found", Response.Status.NOT_FOUND);
        }

        for (FieldValueUpsertItem item : req.items) {
            ContractFieldDef def = findOrCreateDef(clientId, item);

            ContractFieldValue fv = ContractFieldValue.find(
                    "clientId = ?1 and contractVersionId = ?2 and fieldDefId = ?3",
                    clientId, versionId, def.id
            ).firstResult();

            if (fv == null) {
                fv = new ContractFieldValue();
                fv.clientId = clientId;
                fv.contractVersionId = versionId;
                fv.fieldDefId = def.id;
            }

            applyValue(fv, item, def);

            // Si intentan marcar VALIDATED desde extractor y el campo es crítico → exigir evidencia (normalmente todavía no hay)
            // En bulkUpsert lo forzamos a PENDING si es crítico y no hay evidencia aún.
            if ("VALIDATED".equalsIgnoreCase(fv.validationStatus) && "Y".equalsIgnoreCase(def.isCritical)) {
                fv.validationStatus = "PENDING";
            }

            fv.persist();
        }

        AuditLog.of(clientId, "CONTRACT_VERSION", versionId, "FIELDS_BULK_UPSERT", actor,
                "{\"count\":" + req.items.size() + "}"
        ).persist();

        return listValues(clientId, versionId);
    }

    @Transactional
    public EvidenceSnippet addEvidence(long clientId, String actor, long fieldValueId, CreateEvidenceRequest req) {
        ContractFieldValue fv = ContractFieldValue.findById(fieldValueId);
        if (fv == null || !fv.clientId.equals(clientId)) {
            throw new WebApplicationException("Field value not found", Response.Status.NOT_FOUND);
        }

        EvidenceSnippet e = new EvidenceSnippet();
        e.clientId = clientId;
        e.contractVersionId = fv.contractVersionId;
        e.fieldValueId = fv.id;
        e.pageNumber = req.pageNumber;
        e.snippetText = req.snippetText;
        e.bboxJson = req.bboxJson;
        e.charStart = req.charStart;
        e.charEnd = req.charEnd;
        e.sourceType = (req.sourceType == null || req.sourceType.isBlank()) ? "OCR" : req.sourceType.trim().toUpperCase();
        e.persist();

        AuditLog.of(clientId, "FIELD_VALUE", fv.id, "EVIDENCE_ADD", actor,
                "{\"page\":" + (req.pageNumber == null ? "null" : req.pageNumber) + "}"
        ).persist();

        return e;
    }

    @Transactional
    public ContractFieldValue updateValidationStatus(long clientId, String actor, long fieldValueId, UpdateValidationStatusRequest req) {
        ContractFieldValue fv = ContractFieldValue.findById(fieldValueId);
        if (fv == null || !fv.clientId.equals(clientId)) {
            throw new WebApplicationException("Field value not found", Response.Status.NOT_FOUND);
        }

        ContractFieldDef def = ContractFieldDef.findById(fv.fieldDefId);
        if (def == null || !def.clientId.equals(clientId)) {
            throw new WebApplicationException("Field definition not found", Response.Status.BAD_REQUEST);
        }

        String newStatus = req.validationStatus.trim().toUpperCase();

        if ("VALIDATED".equals(newStatus) && "Y".equalsIgnoreCase(def.isCritical)) {
            long evidenceCount = EvidenceSnippet.count("clientId = ?1 and fieldValueId = ?2", clientId, fv.id);
            if (evidenceCount <= 0) {
                throw new WebApplicationException(
                        "Cannot VALIDATE a critical field without evidence",
                        Response.Status.BAD_REQUEST
                );
            }
        }

        fv.validationStatus = newStatus;
        fv.reviewComment = req.reviewComment;
        fv.reviewedBy = actor;
        fv.reviewedAt = LocalDateTime.now();
        fv.persist();

        AuditLog.of(clientId, "FIELD_VALUE", fv.id, "VALIDATION_STATUS_UPDATE", actor,
                "{\"status\":\"" + esc(newStatus) + "\"}"
        ).persist();

        return fv;
    }

    // -------- helpers --------

    private ContractFieldDef findOrCreateDef(long clientId, FieldValueUpsertItem item) {
        String fieldKey = item.fieldKey.trim();

        ContractFieldDef def = ContractFieldDef.find("clientId = ?1 and fieldKey = ?2", clientId, fieldKey).firstResult();
        if (def != null) {
            // si vienen mejoras de metadata, actualiza sin romper
            boolean changed = false;
            if (item.fieldLabel != null && def.fieldLabel == null) { def.fieldLabel = item.fieldLabel; changed = true; }
            if (item.fieldType != null && def.fieldType == null) { def.fieldType = item.fieldType.toUpperCase(); changed = true; }
            if (item.isCritical != null) { def.isCritical = item.isCritical.trim().toUpperCase(); changed = true; }
            if (item.targetAccuracy != null) { def.targetAccuracy = item.targetAccuracy; changed = true; }
            if (changed) def.persist();
            return def;
        }

        // crear def si no existe (útil en fase 1 para no bloquear el pipeline)
        def = new ContractFieldDef();
        def.clientId = clientId;
        def.fieldKey = fieldKey;
        def.fieldLabel = item.fieldLabel;
        def.fieldType = (item.fieldType == null || item.fieldType.isBlank()) ? "TEXT" : item.fieldType.trim().toUpperCase();
        def.isCritical = (item.isCritical == null || item.isCritical.isBlank()) ? "N" : item.isCritical.trim().toUpperCase();
        def.targetAccuracy = item.targetAccuracy;
        def.persist();
        return def;
    }

    private void applyValue(ContractFieldValue fv, FieldValueUpsertItem item, ContractFieldDef def) {
        // limpiar
        fv.valueDate = null;
        fv.valueNumber = null;
        fv.valueText = null;
        fv.valueCurrency = null;
        fv.valueJson = null;

        if (item.confidence != null) fv.confidence = item.confidence;

        String status = (item.validationStatus == null || item.validationStatus.isBlank())
                ? "PENDING" : item.validationStatus.trim().toUpperCase();
        fv.validationStatus = status;

        // set valor según tipo (tolerante)
        String type = def.fieldType == null ? "TEXT" : def.fieldType.toUpperCase();

        switch (type) {
            case "DATE" -> {
                if (item.valueDate != null && !item.valueDate.isBlank()) {
                    fv.valueDate = LocalDate.parse(item.valueDate.trim());
                } else if (item.valueText != null) {
                    // deja en texto si viene sucio
                    fv.valueText = item.valueText;
                }
            }
            case "NUMBER", "PERCENT" -> {
                if (item.valueNumber != null) fv.valueNumber = item.valueNumber;
                else fv.valueText = item.valueText;
            }
            case "MONEY" -> {
                if (item.valueNumber != null) fv.valueNumber = item.valueNumber;
                fv.valueCurrency = item.valueCurrency;
                if (fv.valueNumber == null) fv.valueText = item.valueText;
            }
            case "JSON" -> fv.valueJson = item.valueJson;
            default -> fv.valueText = item.valueText != null ? item.valueText : item.valueJson;
        }
    }

    private static String esc(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}