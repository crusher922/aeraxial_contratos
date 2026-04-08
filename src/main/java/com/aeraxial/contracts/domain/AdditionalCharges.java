package com.aeraxial.contracts.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "additional_charges")
public class AdditionalCharges extends PanacheEntityBase {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "charge_id")
    public Long id;

    @Column(name = "contract_id", nullable = false)
    public Long contractId;

    // MAINTENANCE / UTILITIES / MARKETING / INSURANCE / OTHER
    @Column(name = "charge_type", length = 30)
    public String chargeType;

    @Column(name = "amount", precision = 14, scale = 2)
    public BigDecimal amount;

    @Column(name = "currency", length = 10)
    public String currency = "USD";

    @Column(name = "periodicity", length = 20)
    public String periodicity;

    @Column(name = "notes", length = 2000)
    public String notes;
}
