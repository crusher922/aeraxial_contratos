package com.aeraxial.contracts.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateDocumentRequest {
    public Long siteId;

    public String fileName;
    public String mimeType;

    @NotBlank
    public String storageUri;   // s3://... o https://...

    public String sha256;
    public Long sizeBytes;

    public String isImmutable;  // "Y" o "N" (opcional)
}