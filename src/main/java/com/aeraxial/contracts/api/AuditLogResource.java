package com.aeraxial.contracts.api;

import com.aeraxial.contracts.domain.AuditLog;
import com.aeraxial.contracts.dto.AuditLogResponse;
import com.aeraxial.contracts.dto.CreateAuditLogRequest;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Path("/audit-logs")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuditLogResource {

    @POST
    @Transactional
    public AuditLogResponse create(CreateAuditLogRequest req) {
        if (req.clientId == null) {
            throw new BadRequestException("clientId is required");
        }

        AuditLog log = new AuditLog();
        log.clientId = req.clientId;
        log.entityType = req.entityType;
        log.entityId = req.entityId;
        log.action = req.action;
        log.actor = req.actor;
        log.detailsJson = req.detailsJson;
        log.createdAt = LocalDateTime.now();
        log.persist();

        return AuditLogResponse.fromEntity(log);
    }

    @GET
    public List<AuditLogResponse> list(
            @QueryParam("clientId") Long clientId,
            @QueryParam("createdAtFrom") String createdAtFrom,
            @QueryParam("createdAtTo") String createdAtTo
    ) {
        StringBuilder query = new StringBuilder("1=1");
        List<Object> params = new ArrayList<>();

        if (clientId != null) {
            query.append(" and clientId = ?").append(params.size() + 1);
            params.add(clientId);
        }

        if (createdAtFrom != null && !createdAtFrom.isBlank()) {
            LocalDateTime from = parseStartDate(createdAtFrom);
            query.append(" and createdAt >= ?").append(params.size() + 1);
            params.add(from);
        }

        if (createdAtTo != null && !createdAtTo.isBlank()) {
            LocalDateTime to = parseEndDate(createdAtTo);
            query.append(" and createdAt <= ?").append(params.size() + 1);
            params.add(to);
        }

        query.append(" order by createdAt desc");

        List<AuditLog> logs = AuditLog.list(query.toString(), params.toArray());

        return logs.stream()
                .map(AuditLogResponse::fromEntity)
                .toList();
    }

    @GET
    @Path("/{id}")
    public AuditLogResponse getById(@PathParam("id") Long id) {
        AuditLog log = AuditLog.findById(id);
        if (log == null) {
            throw new NotFoundException("Audit log not found");
        }
        return AuditLogResponse.fromEntity(log);
    }

    private LocalDateTime parseStartDate(String value) {
        if (value.length() == 10) {
            return LocalDate.parse(value).atStartOfDay();
        }
        return LocalDateTime.parse(value);
    }

    private LocalDateTime parseEndDate(String value) {
        if (value.length() == 10) {
            return LocalDate.parse(value).atTime(LocalTime.MAX);
        }
        return LocalDateTime.parse(value);
    }
}