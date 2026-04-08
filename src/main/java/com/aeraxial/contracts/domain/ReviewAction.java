package com.aeraxial.contracts.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "review_action",
       indexes = @Index(name = "idx_ra_fv", columnList = "field_value_id"))
public class ReviewAction extends PanacheEntityBase {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "action_id")
    public Long id;

    @Column(name = "contract_version_id", nullable = false)
    public Long contractVersionId;

    @Column(name = "field_value_id", nullable = false)
    public Long fieldValueId;

    @Column(name = "client_id", nullable = false)
    public Long clientId;

    // APPROVE / REJECT / EDIT / OVERRIDE
    @Column(name = "action_type", nullable = false, length = 20)
    public String actionType;

    @Column(name = "old_value_json", columnDefinition = "json")
    public String oldValueJson;

    @Column(name = "new_value_json", columnDefinition = "json")
    public String newValueJson;

    @Column(name = "comment_text", length = 1000)
    public String commentText;

    @Column(name = "actor", length = 120)
    public String actor;

    @Column(name = "created_at")
    public LocalDateTime createdAt;

    @PrePersist
    void prePersist() { this.createdAt = LocalDateTime.now(); }
}
