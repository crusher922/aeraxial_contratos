package com.aeraxial.contracts.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "payment_terms")
public class PaymentTerms extends PanacheEntityBase {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_terms_id")
    public Long id;

    @Column(name = "contract_id", nullable = false)
    public Long contractId;

    // TRANSFER / CARD / CASH / OTHER
    @Column(name = "payment_method", length = 30)
    public String paymentMethod;

    @Column(name = "payment_terms_text", columnDefinition = "TEXT")
    public String paymentTermsText;

    @Column(name = "grace_period_days")
    public Integer gracePeriodDays;

    // FIXED / PERCENT
    @Column(name = "late_fee_type", length = 20)
    public String lateFeeType;

    @Column(name = "late_fee_amount", precision = 14, scale = 2)
    public BigDecimal lateFeeAmount;

    @Column(name = "late_fee_percent", precision = 6, scale = 3)
    public BigDecimal lateFeePercent;

    @Column(name = "late_interest_percent", precision = 6, scale = 3)
    public BigDecimal lateInterestPercent;
}
