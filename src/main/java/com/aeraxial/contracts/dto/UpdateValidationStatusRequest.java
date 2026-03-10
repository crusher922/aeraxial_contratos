package com.aeraxial.contracts.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdateValidationStatusRequest {
    @NotBlank
    public String validationStatus; // PENDING/VALIDATED/REJECTED/OVERRIDDEN
    public String reviewComment;
}