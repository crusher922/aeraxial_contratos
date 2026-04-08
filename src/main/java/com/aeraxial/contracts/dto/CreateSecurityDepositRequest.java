package com.aeraxial.contracts.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CreateSecurityDepositRequest {
    public BigDecimal amount;
    public String currency = "USD";
    public LocalDate dueDate;
    public String refundConditions;
}
