package com.aeraxial.contracts.dto;

import java.time.LocalDateTime;

public class UpdateReviewTaskRequest {
    public String assignedTo;
    public String status;    // OPEN / IN_PROGRESS / DONE / CANCELLED
    public String priority;  // LOW / MEDIUM / HIGH
    public LocalDateTime dueAt;
    public LocalDateTime completedAt;
}
