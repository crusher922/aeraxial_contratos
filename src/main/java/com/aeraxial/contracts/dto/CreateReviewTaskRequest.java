package com.aeraxial.contracts.dto;

import java.time.LocalDateTime;

public class CreateReviewTaskRequest {
    public Long contractVersionId;
    public Long clientId;
    public String assignedTo;
    public String priority = "MEDIUM";  // LOW / MEDIUM / HIGH
    public LocalDateTime dueAt;
}
