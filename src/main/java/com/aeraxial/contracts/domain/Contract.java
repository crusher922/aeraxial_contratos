package com.aeraxial.contracts.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "contract",
        uniqueConstraints = @UniqueConstraint(name="uq_contract", columnNames={"client_id","contract_number"}))
public class Contract extends PanacheEntityBase {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="contract_id")
    public Long id;

    @Column(name="client_id", nullable=false)
    public Long clientId;

    @Column(name="site_id")
    public Long siteId;

    @Column(name="contract_number", nullable=false, length=80)
    public String contractNumber;

    @Column(name="contract_type", nullable=false, length=50)
    public String contractType;

    @Column(name="category", length=80)
    public String category;

    @Column(name="sign_date")
    public LocalDate signDate;

    @Column(name="start_date")
    public LocalDate startDate;

    @Column(name="end_date")
    public LocalDate endDate;

    @Column(name="duration_months")
    public Integer durationMonths;

    @Column(name="status", nullable=false, length=30)
    public String status = "DRAFT";

    @Column(name="created_at", nullable=false)
    public LocalDateTime createdAt;

    @Column(name="updated_at")
    public LocalDateTime updatedAt;

    @PrePersist
    void prePersist() { this.createdAt = LocalDateTime.now(); }

    @PreUpdate
    void preUpdate() { this.updatedAt = LocalDateTime.now(); }
}