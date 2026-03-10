package com.aeraxial.contracts.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdateVersionStatusRequest {
    @NotBlank
    public String pipelineStatus;
}
