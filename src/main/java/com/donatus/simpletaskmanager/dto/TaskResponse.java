package com.donatus.simpletaskmanager.dto;

import com.donatus.simpletaskmanager.models.TaskStatus;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

/**
 * DTO for {@link com.donatus.simpletaskmanager.models.TaskEntity}
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TaskResponse {
    private Long id;
    private Long userId;
    private Integer periodInDays;
    private Timestamp startDate;
    private TaskStatus status;
    private String taskTitle;
    private String taskDetails;
    private String createdBy;
    private String assignBy;
}