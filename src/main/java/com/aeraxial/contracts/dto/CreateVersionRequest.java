package com.aeraxial.contracts.dto;

import jakarta.validation.constraints.NotNull;

public class CreateVersionRequest {
    @NotNull
    public Long sourceDocumentId;  // document_object.document_id

    public Long ocrDocumentId;     // opcional
    public String pipelineStatus;  // UPLOADED/OCR_DONE/EXTRACTED/IN_REVIEW/APPROVED
    public String extractionJson;  // opcional (JSON string)
    public String modelInfoJson;   // opcional (JSON string)
}