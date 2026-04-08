package com.aeraxial.contracts.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "tenant_obligation",
       indexes = @Index(name = "idx_obl_contract", columnList = "contract_id"))
public class TenantObligation extends PanacheEntityBase {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "obligation_id")
    public Long id;

    @Column(name = "contract_id", nullable = false)
    public Long contractId;

    // HOURS / MAINTENANCE / INSURANCE / PERMITS / OPS_STANDARDS
    @Column(name = "obligation_type", nullable = false, length = 40)
    public String obligationType;

    @Column(name = "details", columnDefinition = "LONGTEXT")
    public String details;
}
