package com.aeraxial.contracts.dto;

import java.math.BigDecimal;

public class CreateRentVariableRequest {
    public BigDecimal percentage;
    public String thresholdJson;
    public BigDecimal minGuaranteeAmount;
    public String minGuaranteeCurrency;
    public String notes;
}
