package com.aeraxial.contracts.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "clause",
       indexes = @Index(name = "idx_clause_contract", columnList = "contract_id"))
public class Clause extends PanacheEntityBase {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "clause_id")
    public Long id;

    @Column(name = "contract_id", nullable = false)
    public Long contractId;

    @Column(name = "clause_type", nullable = false, length = 40)
    public String clauseType;

    @Column(name = "title", length = 300)
    public String title;

    @Column(name = "details", columnDefinition = "LONGTEXT")
    public String details;

    // Y / N
    @Column(name = "is_critical", length = 1)
    public String isCritical = "N";
}
