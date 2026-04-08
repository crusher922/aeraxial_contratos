package com.aeraxial.contracts.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "security_deposit")
public class SecurityDeposit extends PanacheEntityBase {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deposit_id")
    public Long id;

    @Column(name = "contract_id", nullable = false)
    public Long contractId;

    @Column(name = "amount", precision = 14, scale = 2)
    public BigDecimal amount;

    @Column(name = "currency", length = 10)
    public String currency = "USD";

    @Column(name = "due_date")
    public LocalDate dueDate;

    @Column(name = "refund_conditions", length = 2000)
    public String refundConditions;
}
