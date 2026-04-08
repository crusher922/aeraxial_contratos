package com.aeraxial.contracts.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "review_task",
       indexes = {
           @Index(name = "idx_rt_cv",     columnList = "contract_version_id"),
           @Index(name = "idx_rt_status", columnList = "client_id,status")
       })
public class ReviewTask extends PanacheEntityBase {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    public Long id;

    @Column(name = "contract_version_id", nullable = false)
    public Long contractVersionId;

    @Column(name = "client_id", nullable = false)
    public Long clientId;

    @Column(name = "assigned_to", length = 120)
    public String assignedTo;

    // OPEN / IN_PROGRESS / DONE / CANCELLED
    @Column(name = "status", nullable = false, length = 20)
    public String status = "OPEN";

    // LOW / MEDIUM / HIGH
    @Column(name = "priority", nullable = false, length = 10)
    public String priority = "MEDIUM";

    @Column(name = "created_at")
    public LocalDateTime createdAt;

    @Column(name = "due_at")
    public LocalDateTime dueAt;

    @Column(name = "completed_at")
    public LocalDateTime completedAt;

    @PrePersist
    void prePersist() { this.createdAt = LocalDateTime.now(); }
}
