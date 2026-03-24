package com.aeraxial.masterdata.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log")
public class AuditLog extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audit_id")
    public Long id;

    @Column(name = "client_id", nullable = false)
    public Long clientId;

    @Column(name = "entity_type", length = 100)
    public String entityType;

    @Column(name = "entity_id")
    public Long entityId;

    @Column(name = "action", length = 100)
    public String action;

    @Column(name = "actor", length = 150)
    public String actor;

    @Column(name = "details_json", columnDefinition = "TEXT")
    public String detailsJson;

    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}