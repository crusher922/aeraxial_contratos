package com.aeraxial.contracts.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="audit_log", indexes = {
        @Index(name="idx_audit_client", columnList="client_id,created_at")
})
public class AuditLog extends PanacheEntityBase {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="audit_id")
    public Long id;

    @Column(name="client_id", nullable=false)
    public Long clientId;

    @Column(name="entity_type", nullable=false, length=50)
    public String entityType;

    @Column(name="entity_id", nullable=false)
    public Long entityId;

    @Column(name="action", nullable=false, length=50)
    public String action;

    @Column(name="actor", length=120)
    public String actor;

    @Column(name="details_json", columnDefinition="json")
    public String detailsJson;

    @Column(name="created_at", nullable=false)
    public LocalDateTime createdAt;

    @PrePersist
    void prePersist() { this.createdAt = LocalDateTime.now(); }

    public static AuditLog of(Long clientId, String entityType, Long entityId, String action, String actor, String detailsJson) {
        AuditLog a = new AuditLog();
        a.clientId = clientId;
        a.entityType = entityType;
        a.entityId = entityId;
        a.action = action;
        a.actor = actor;
        a.detailsJson = detailsJson;
        return a;
    }
}