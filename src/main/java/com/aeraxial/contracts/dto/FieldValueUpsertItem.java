package com.aeraxial.contracts.dto;

import jakarta.validation.constraints.NotBlank;

public class FieldValueUpsertItem {
    // Recomiendo usar fieldKey para acoplarte al JSON schema (term.start_date, economic.fixed_rent.amount, etc.)
    @NotBlank
    public String fieldKey;

    public String fieldLabel;     // opcional (si no existe en def)
    public String fieldType;      // DATE/NUMBER/MONEY/PERCENT/TEXT/ENUM/JSON (opcional)
    public String isCritical;     // "Y"/"N" (opcional)
    public Double targetAccuracy; // opcional

    // valores (manda solo lo que aplique)
    public String valueDate;      // "YYYY-MM-DD"
    public Double valueNumber;
    public String valueText;
    public String valueCurrency;  // "USD"
    public String valueJson;      // JSON string (cuando fieldType=JSON)

    public Double confidence;     // 0..1
    public String validationStatus; // PENDING/VALIDATED/REJECTED/OVERRIDDEN (opcional; default PENDING)
}