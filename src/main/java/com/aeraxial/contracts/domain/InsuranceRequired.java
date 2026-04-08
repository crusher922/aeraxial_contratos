package com.aeraxial.contracts.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "insurance_required",
       indexes = @Index(name = "idx_ins_contract", columnList = "contract_id"))
public class InsuranceRequired extends PanacheEntityBase {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ins_req_id")
    public Long id;

    @Column(name = "contract_id", nullable = false)
    public Long contractId;

    @Column(name = "insurance_type", nullable = false, length = 100)
    public String insuranceType;

    @Column(name = "coverage_amount", precision = 14, scale = 2)
    public BigDecimal coverageAmount;

    @Column(name = "currency", length = 10)
    public String currency;

    @Column(name = "valid_from")
    public LocalDate validFrom;

    @Column(name = "valid_to")
    public LocalDate validTo;

    @Column(name = "notes", length = 2000)
    public String notes;
}
