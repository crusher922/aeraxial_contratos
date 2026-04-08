package com.aeraxial.contracts.dto;

import java.math.BigDecimal;

public class CreateRentBaseRequest {
    public BigDecimal amount;
    public String currency    = "USD";
    public String periodicity = "MONTHLY";
    public Integer paymentDueDay;
}
