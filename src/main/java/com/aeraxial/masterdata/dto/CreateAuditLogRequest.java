package com.aeraxial.masterdata.dto;

public class CreateAuditLogRequest {
    public Long clientId;
    public String entityType;
    public Long entityId;
    public String action;
    public String actor;
    public String detailsJson;
}