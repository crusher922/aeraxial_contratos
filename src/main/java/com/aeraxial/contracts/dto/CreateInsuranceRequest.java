package com.aeraxial.contracts.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CreateInsuranceRequest {
    public String insuranceType;
    public BigDecimal coverageAmount;
    public String currency;
    public LocalDate validFrom;
    public LocalDate validTo;
    public String notes;
}
