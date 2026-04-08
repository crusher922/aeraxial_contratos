package com.aeraxial.contracts.dto;

import java.math.BigDecimal;

public class CreateAdditionalChargeRequest {
    public String chargeType;   // MAINTENANCE / UTILITIES / MARKETING / INSURANCE / OTHER
    public BigDecimal amount;
    public String currency    = "USD";
    public String periodicity;
    public String notes;
}
