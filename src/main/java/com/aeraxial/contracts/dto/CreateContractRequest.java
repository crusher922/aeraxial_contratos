package com.aeraxial.contracts.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class CreateContractRequest {
    public Long siteId;

    @NotBlank
    public String contractNumber;

    @NotBlank
    public String contractType;

    public String category;

    public LocalDate signDate;
    public LocalDate startDate;
    public LocalDate endDate;

    public Integer durationMonths;
}
