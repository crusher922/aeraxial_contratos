package com.aeraxial.contracts.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "rent_variable")
public class RentVariable extends PanacheEntityBase {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rent_variable_id")
    public Long id;

    @Column(name = "contract_id", nullable = false)
    public Long contractId;

    @Column(name = "percentage", precision = 6, scale = 3)
    public BigDecimal percentage;

    @Column(name = "threshold_json", columnDefinition = "json")
    public String thresholdJson;

    @Column(name = "min_guarantee_amount", precision = 14, scale = 2)
    public BigDecimal minGuaranteeAmount;

    @Column(name = "min_guarantee_currency", length = 10)
    public String minGuaranteeCurrency;

    @Column(name = "notes", length = 2000)
    public String notes;
}
