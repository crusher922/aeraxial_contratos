package com.aeraxial.contracts.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="evidence_snippet", indexes = @Index(name="idx_ev_fv", columnList="field_value_id"))
public class EvidenceSnippet extends PanacheEntityBase {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="evidence_id")
    public Long id;

    @Column(name="contract_version_id", nullable=false)
    public Long contractVersionId;

    @Column(name="field_value_id", nullable=false)
    public Long fieldValueId;

    @Column(name="client_id", nullable=false)
    public Long clientId;

    @Column(name="page_number")
    public Integer pageNumber;

    @Column(name="snippet_text", length=4000)
    public String snippetText;

    @Column(name="bbox_json", columnDefinition="json")
    public String bboxJson;

    @Column(name="char_start")
    public Integer charStart;

    @Column(name="char_end")
    public Integer charEnd;

    @Column(name="source_type", length=20)
    public String sourceType = "OCR";

    @Column(name="created_at", nullable=false)
    public LocalDateTime createdAt;

    @PrePersist
    void prePersist() { this.createdAt = LocalDateTime.now(); }
}