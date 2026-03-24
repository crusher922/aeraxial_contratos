package com.aeraxial.masterdata.dto;

import com.aeraxial.masterdata.domain.AuditLog;

import java.time.LocalDateTime;

public class AuditLogResponse {
    public Long auditId;
    public Long clientId;
    public String entityType;
    public Long entityId;
    public String action;
    public String actor;
    public String detailsJson;
    public LocalDateTime createdAt;

    public static AuditLogResponse fromEntity(AuditLog log) {
        AuditLogResponse r = new AuditLogResponse();
        r.auditId = log.id;
        r.clientId = log.clientId;
        r.entityType = log.entityType;
        r.entityId = log.entityId;
        r.action = log.action;
        r.actor = log.actor;
        r.detailsJson = log.detailsJson;
        r.createdAt = log.createdAt;
        return r;
    }
}