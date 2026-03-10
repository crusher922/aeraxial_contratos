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
        // Validación básica de negocio
        if (req.startDate != null && req.endDate != null && req.endDate.isBefore(req.startDate)) {
            throw new WebApplicationException("endDate cannot be before startDate", Response.Status.BAD_REQUEST);
        }

        // Unicidad por clientId + contractNumber
        Contract existing = Contract.find("clientId = ?1 and contractNumber = ?2", clientId, req.contractNumber).firstResult();
        if (existing != null) {
            throw new WebApplicationException("Contract number already exists for this client", Response.Status.CONFLICT);
        }

        Contract c = new Contract();
        c.clientId = clientId;
        c.siteId = req.siteId;
        c.contractNumber = req.contractNumber;
        c.contractType = req.contractType;
        c.category = req.category;
        c.signDate = req.signDate;
        c.startDate = req.startDate;
        c.endDate = req.endDate;
        c.durationMonths = req.durationMonths;
        c.status = "DRAFT";
        c.persist();

        AuditLog.of(clientId, "CONTRACT", c.id, "CREATE", actor,
                "{\"contractNumber\":\"" + escape(req.contractNumber) + "\"}"
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