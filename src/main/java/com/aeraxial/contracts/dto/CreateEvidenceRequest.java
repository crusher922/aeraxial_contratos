package com.aeraxial.contracts.dto;

public class CreateEvidenceRequest {
    public Integer pageNumber;
    public String snippetText;
    public String bboxJson;   // JSON string
    public Integer charStart;
    public Integer charEnd;
    public String sourceType; // OCR/NATIVE_PDF_TEXT/MANUAL
}