package com.aeraxial.contracts.dto;

public class CreateReviewActionRequest {
    public Long contractVersionId;
    public Long fieldValueId;
    public Long clientId;
    public String actionType;    // APPROVE / REJECT / EDIT / OVERRIDE
    public String oldValueJson;
    public String newValueJson;
    public String commentText;
    public String actor;
}
