package com.aeraxial.contracts.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class BulkUpsertFieldValuesRequest {
    @NotEmpty
    @Valid
    public List<FieldValueUpsertItem> items;
}