package com.aeraxial.contracts.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "rent_base")
public class RentBase extends PanacheEntityBase {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long id;

    @Column(name = "contract_id", nullable = false)
    public Long contractId;

    @Column(name = "amount", precision = 14, scale = 2)
    public BigDecimal amount;

    @Column(name = "currency", length = 10)
    public String currency = "USD";

    // MONTHLY / QUARTERLY / ANNUAL
    @Column(name = "periodicity", length = 20)
    public String periodicity = "MONTHLY";

    @Column(name = "payment_due_day")
    public Integer paymentDueDay;
}
