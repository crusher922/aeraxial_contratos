package com.aeraxial.contracts.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="contract_version",
        uniqueConstraints = @UniqueConstraint(name="uq_contract_ver", columnNames={"contract_id","version_number"}),
        indexes = {
                @Index(name="idx_cv_contract", columnList="contract_id,version_number"),
                @Index(name="idx_cv_client", columnList="client_id,pipeline_status")
        }
)
public class ContractVersion extends PanacheEntityBase {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="contract_version_id")
    public Long id;

    @Column(name="contract_id", nullable=false)
    public Long contractId;

    @Column(name="client_id", nullable=false)
    public Long clientId;

    @Column(name="site_id")
    public Long siteId;

    @Column(name="version_number", nullable=false)
    public Integer versionNumber;

    @Column(name="source_document_id", nullable=false)
    public Long sourceDocumentId;

    @Column(name="ocr_document_id")
    public Long ocrDocumentId;

    @Column(name="extraction_json", columnDefinition="json")
    public String extractionJson;

    @Column(name="model_info_json", columnDefinition="json")
    public String modelInfoJson;

    @Column(name="pipeline_status", nullable=false, length=30)
    public String pipelineStatus = "UPLOADED";

    @Column(name="created_at", nullable=false)
    public LocalDateTime createdAt;

    @Column(name="created_by", length=120)
    public String createdBy;

    @PrePersist
    void prePersist() { this.createdAt = LocalDateTime.now(); }
}
