package com.aeraxial.contracts.dto;

public class CreateClauseRequest {
    public String clauseType;
    public String title;
    public String details;
    public String isCritical = "N";  // Y / N
}
