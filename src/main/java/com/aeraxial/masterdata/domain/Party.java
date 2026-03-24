package com.aeraxial.masterdata.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "party")
public class Party extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "party_id")
    private Long id;

    @Column(name = "client_id", nullable = false)
    public Long clientId;

    @Column(name = "party_type", nullable = false, length = 30)
    public String partyType;

    @Column(name = "legal_name", length = 250)
    public String legalName;

    @Column(name = "trade_name", length = 250)
    public String tradeName;

    @Column(name = "tax_id", length = 50)
    public String taxId;

    @Column(name = "econ_activity", length = 200)
    public String econActivity;

    @Column(name = "business_category", length = 120)
    public String businessCategory;

    @Column(name = "contact_name", length = 200)
    public String contactName;

    @Column(name = "contact_email", length = 200)
    public String contactEmail;

    @Column(name = "contact_phone", length = 50)
    public String contactPhone;

    @Column(name = "legal_representative", length = 200)
    public String legalRepresentative;

    @Column(name = "created_at")
    public LocalDateTime createdAt;

    @PrePersist
    void prePersist(){
        this.createdAt = LocalDateTime.now();
    }
}
