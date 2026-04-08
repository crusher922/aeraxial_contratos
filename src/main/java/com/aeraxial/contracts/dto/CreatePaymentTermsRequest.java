package com.aeraxial.contracts.dto;

import java.math.BigDecimal;

public class CreatePaymentTermsRequest {
    public String paymentMethod;       // TRANSFER / CARD / CASH / OTHER
    public String paymentTermsText;
    public Integer gracePeriodDays;
    public String lateFeeType;         // FIXED / PERCENT
    public BigDecimal lateFeeAmount;
    public BigDecimal lateFeePercent;
    public BigDecimal lateInterestPercent;
}
