package com.donatus.simpletaskmanager.dto;

import com.donatus.simpletaskmanager.models.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

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
    private String firstName;
    private String lastName;
    private String email;
    private Integer periodInDays;
    private Timestamp startDate;
    private TaskStatus status;
    private String taskTitle;
    private String taskDetails;
    private String createdBy;
    private String assignBy;
    private LocalDateTime dateCreated;
    private LocalDateTime dateModified;
}