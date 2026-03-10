package com.aeraxial.contracts.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="document_object", indexes = {
        @Index(name="idx_doc_client", columnList="client_id"),
        @Index(name="idx_doc_site", columnList="site_id"),
        @Index(name="idx_doc_sha", columnList="sha256")
})
public class DocumentObject extends PanacheEntityBase {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="document_id")
    public Long id;

    @Column(name="client_id", nullable=false)
    public Long clientId;

    @Column(name="site_id")
    public Long siteId;

    @Column(name="file_name", length=255)
    public String fileName;

    @Column(name="mime_type", length=100)
    public String mimeType;

    @Column(name="storage_uri", nullable=false, length=1000)
    public String storageUri;

    @Column(name="sha256", length=64)
    public String sha256;

    @Column(name="size_bytes")
    public Long sizeBytes;

    @Column(name="is_immutable", length=1)
    public String isImmutable = "Y";

    @Column(name="created_at", nullable=false)
    public LocalDateTime createdAt;

    @Column(name="created_by", length=120)
    public String createdBy;

    @PrePersist
    void prePersist() { this.createdAt = LocalDateTime.now(); }
}