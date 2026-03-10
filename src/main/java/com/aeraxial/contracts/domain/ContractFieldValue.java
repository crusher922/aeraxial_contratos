package com.aeraxial.contracts.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="contract_field_value", indexes = {
        @Index(name="idx_fv_cv", columnList="contract_version_id"),
        @Index(name="idx_fv_status", columnList="client_id,validation_status")
})
public class ContractFieldValue extends PanacheEntityBase {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="field_value_id")
    public Long id;

    @Column(name="contract_version_id", nullable=false)
    public Long contractVersionId;

    @Column(name="client_id", nullable=false)
    public Long clientId;

    @Column(name="field_def_id", nullable=false)
    public Long fieldDefId;

    @Column(name="value_date")
    public LocalDate valueDate;

    @Column(name="value_number")
    public Double valueNumber;

    @Column(name="value_text", length=4000)
    public String valueText;

    @Column(name="value_currency", length=10)
    public String valueCurrency;

    @Column(name="value_json", columnDefinition="json")
    public String valueJson;

    @Column(name="confidence")
    public Double confidence;

    @Column(name="validation_status", nullable=false, length=20)
    public String validationStatus = "PENDING";

    @Column(name="reviewed_by", length=120)
    public String reviewedBy;

    @Column(name="reviewed_at")
    public LocalDateTime reviewedAt;

    @Column(name="review_comment", length=1000)
    public String reviewComment;
}