package com.aeraxial.contracts.service;

import com.aeraxial.contracts.domain.*;
import com.aeraxial.contracts.dto.CreateDocumentRequest;
import com.aeraxial.contracts.dto.CreateVersionRequest;
import com.aeraxial.contracts.dto.UpdateVersionStatusRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.util.List;

@ApplicationScoped
public class VersionService {

    public List<ContractVersion> listVersions(long clientId, long contractId) {
        // No mostrar versiones de otro cliente
        Contract c = Contract.findById(contractId);
        if (c == null || !c.clientId.equals(clientId)) {
            throw new WebApplicationException("Contract not found", Response.Status.NOT_FOUND);
        }
        return ContractVersion.list("clientId = ?1 and contractId = ?2 order by versionNumber desc", clientId, contractId);
    }

    public ContractVersion getVersion(long clientId, long versionId) {
        ContractVersion v = ContractVersion.findById(versionId);
        if (v == null || !v.clientId.equals(clientId)) {
            throw new WebApplicationException("Version not found", Response.Status.NOT_FOUND);
        }
        return v;
    }
    public List<DocumentObject> getAllDocuments(long clientId) {
        return DocumentObject
                .find("clientId = ?1 order by createdAt desc", clientId)
                .list();
    }

    @Transactional
    public DocumentObject createDocument(long clientId, String actor, CreateDocumentRequest req) {
        DocumentObject d = new DocumentObject();
        d.clientId = clientId;
        d.siteId = req.siteId;
        d.fileName = req.fileName;
        d.mimeType = req.mimeType;
        d.storageUri = req.storageUri;
        d.sha256 = req.sha256;
        d.sizeBytes = req.sizeBytes;
        d.isImmutable = (req.isImmutable == null || req.isImmutable.isBlank()) ? "Y" : req.isImmutable.trim().toUpperCase();
        d.createdBy = actor;
        d.persist();

        AuditLog.of(clientId, "DOCUMENT", d.id, "CREATE", actor,
                "{\"storageUri\":\"" + esc(req.storageUri) + "\"}"
        ).persist();

        return d;
    }

    @Transactional
    public ContractVersion createVersion(long clientId, String actor, long contractId, CreateVersionRequest req) {
        Contract c = Contract.findById(contractId);
        if (c == null || !c.clientId.equals(clientId)) {
            throw new WebApplicationException("Contract not found", Response.Status.NOT_FOUND);
        }

        DocumentObject src = DocumentObject.findById(req.sourceDocumentId);
        if (src == null || !src.clientId.equals(clientId)) {
            throw new WebApplicationException("Source document not found for this client", Response.Status.BAD_REQUEST);
        }

        if (req.ocrDocumentId != null) {
            DocumentObject ocr = DocumentObject.findById(req.ocrDocumentId);
            if (ocr == null || !ocr.clientId.equals(clientId)) {
                throw new WebApplicationException("OCR document not found for this client", Response.Status.BAD_REQUEST);
            }
        }

        // version_number = max + 1
        Integer maxVer = ContractVersion.find("select coalesce(max(versionNumber), 0) from ContractVersion where contractId = ?1", contractId)
                .project(Integer.class)
                .singleResult();
        int next = maxVer + 1;

        ContractVersion v = new ContractVersion();
        v.contractId = contractId;
        v.clientId = clientId;
        v.siteId = c.siteId;
        v.versionNumber = next;
        v.sourceDocumentId = req.sourceDocumentId;
        v.ocrDocumentId = req.ocrDocumentId;

        if (req.pipelineStatus != null && !req.pipelineStatus.isBlank()) {
            v.pipelineStatus = req.pipelineStatus.trim().toUpperCase();
        } else {
            v.pipelineStatus = "UPLOADED";
        }

        // Estos 2 son opcionales (pueden venir más tarde en PATCH)
        v.extractionJson = req.extractionJson;
        v.modelInfoJson = req.modelInfoJson;

        v.createdBy = actor;
        v.persist();

        AuditLog.of(clientId, "CONTRACT_VERSION", v.id, "CREATE", actor,
                "{\"contractId\":" + contractId + ",\"versionNumber\":" + next + "}"
        ).persist();

        return v;
    }

    @Transactional
    public ContractVersion updateVersionStatus(long clientId, String actor, long versionId, UpdateVersionStatusRequest req) {
        ContractVersion v = getVersion(clientId, versionId);
        v.pipelineStatus = req.pipelineStatus.trim().toUpperCase();
        v.persist();

        AuditLog.of(clientId, "CONTRACT_VERSION", v.id, "STATUS_UPDATE", actor,
                "{\"pipelineStatus\":\"" + esc(v.pipelineStatus) + "\"}"
        ).persist();

        return v;
    }

    @Transactional
    public ContractVersion patchVersionPayloads(long clientId, String actor, long versionId, String extractionJson, String modelInfoJson, Long ocrDocumentId) {
        ContractVersion v = getVersion(clientId, versionId);

        if (ocrDocumentId != null) {
            DocumentObject ocr = DocumentObject.findById(ocrDocumentId);
            if (ocr == null || !ocr.clientId.equals(clientId)) {
                throw new WebApplicationException("OCR document not found for this client", Response.Status.BAD_REQUEST);
            }
            v.ocrDocumentId = ocrDocumentId;
        }
        if (extractionJson != null) v.extractionJson = extractionJson;
        if (modelInfoJson != null) v.modelInfoJson = modelInfoJson;

        v.persist();

        AuditLog.of(clientId, "CONTRACT_VERSION", v.id, "UPDATE", actor, "{\"patch\":true}").persist();
        return v;
    }

//    public List<DocumentObject> listDocuments(long clientId, Long siteId, String fileName) {
//        StringBuilder query = new StringBuilder("clientId = ?1");
//        java.util.List<Object> params = new java.util.ArrayList<>();
//        params.add(clientId);
//
//        if (siteId != null) {
//            query.append(" and siteId = ?").append(params.size() + 1);
//            params.add(siteId);
//        }
//
//        if (fileName != null && !fileName.isBlank()) {
//            query.append(" and lower(fileName) like ?").append(params.size() + 1);
//            params.add("%" + fileName.trim().toLowerCase() + "%");
//        }
//
//        query.append(" order by createdAt desc");
//
//        return DocumentObject.list(query.toString(), params.toArray());
//    }

    private static String esc(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}