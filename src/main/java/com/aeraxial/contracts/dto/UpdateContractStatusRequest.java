package com.aeraxial.contracts.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdateContractStatusRequest {
    @NotBlank
    public String status; // DRAFT/REVIEWED/ACTIVE/EXPIRED/TERMINATED
}