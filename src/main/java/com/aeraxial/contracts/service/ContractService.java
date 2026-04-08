package com.aeraxial.contracts.service;

import com.aeraxial.contracts.domain.AuditLog;
import com.aeraxial.contracts.domain.Contract;
import com.aeraxial.contracts.dto.CreateContractRequest;
import com.aeraxial.contracts.dto.UpdateContractStatusRequest;
import io.quarkus.hibernate.orm.panache.Panache;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.util.List;

@ApplicationScoped
public class ContractService {

    @Transactional
    public Contract create(long clientId, String actor, CreateContractRequest req) {

        if (req.startDate != null && req.endDate != null && req.endDate.isBefore(req.startDate)) {
            throw new WebApplicationException("endDate cannot be before startDate", Response.Status.BAD_REQUEST);
        }

        // 🔥 FIX AQUÍ
        String contractNumber = req.contractNumber;

        if (contractNumber == null || contractNumber.isBlank() || "UNKNOWN".equalsIgnoreCase(contractNumber)) {
            contractNumber = "UNKNOWN-" + java.util.UUID.randomUUID().toString().substring(0, 8);
        }

        // 🔥 VALIDAS CON EL NUEVO VALOR
        Contract existing = Contract.find(
                "clientId = ?1 and contractNumber = ?2",
                clientId,
                contractNumber
        ).firstResult();

        if (existing != null) {
            throw new WebApplicationException("Contract number already exists for this client", Response.Status.CONFLICT);
        }

        Contract c = new Contract();
        c.clientId = clientId;
        c.siteId = req.siteId;
        c.contractNumber = contractNumber; // 👈 usar el corregido
        c.contractType = req.contractType;
        c.category = req.category;
        c.signDate = req.signDate;
        c.startDate = req.startDate;
        c.endDate = req.endDate;
        c.durationMonths = req.durationMonths;
        c.status = "DRAFT";
        c.persist();

        AuditLog.of(clientId, "CONTRACT", c.id, "CREATE", actor,
                "{\"contractNumber\":\"" + escape(contractNumber) + "\"}"
        ).persist();

        return c;
    }

    public List<Contract> search(long clientId, String status) {
        if (status == null || status.isBlank()) {
            return Contract.list("clientId = ?1", clientId);
        }
        return Contract.list("clientId = ?1 and status = ?2", clientId, status.trim());
    }

    public Contract get(long clientId, long contractId) {
        Contract c = Contract.findById(contractId);
        if (c == null || !c.clientId.equals(clientId)) {
            throw new WebApplicationException("Contract not found", Response.Status.NOT_FOUND);
        }
        return c;
    }

    @Transactional
    public Contract updateStatus(long clientId, String actor, long contractId, UpdateContractStatusRequest req) {
        Contract c = get(clientId, contractId);
        c.status = req.status.trim().toUpperCase();
        c.persist();

        AuditLog.of(clientId, "CONTRACT", c.id, "STATUS_UPDATE", actor,
                "{\"status\":\"" + escape(c.status) + "\"}"
        ).persist();

        return c;
    }

    private static String escape(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}