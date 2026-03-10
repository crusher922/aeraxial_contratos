package com.aeraxial.contracts.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name="contract_field_def",
        uniqueConstraints = @UniqueConstraint(name="uq_field", columnNames={"client_id","field_key"}),
        indexes = @Index(name="idx_field_client", columnList="client_id")
)
public class ContractFieldDef extends PanacheEntityBase {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="field_def_id")
    public Long id;

    @Column(name="client_id", nullable=false)
    public Long clientId;

    @Column(name="field_key", nullable=false, length=200)
    public String fieldKey;

    @Column(name="field_label", length=200)
    public String fieldLabel;

    @Column(name="field_type", nullable=false, length=30)
    public String fieldType; // DATE/NUMBER/MONEY/PERCENT/TEXT/ENUM/JSON

    @Column(name="is_critical", length=1)
    public String isCritical = "N";

    @Column(name="target_accuracy")
    public Double targetAccuracy;
}
