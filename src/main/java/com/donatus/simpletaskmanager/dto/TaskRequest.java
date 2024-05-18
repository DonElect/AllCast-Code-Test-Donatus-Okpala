package com.donatus.simpletaskmanager.dto;

import com.donatus.simpletaskmanager.models.TaskEntity;
import com.donatus.simpletaskmanager.models.TaskStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.sql.Timestamp;

/**
 * DTO for {@link TaskEntity}
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class TaskRequest {
    @NotNull(message = "Period should not be empty")
    @Min(message = "Period should be greater than 1 day", value = 1)
    @Max(message = "Period should be less than 7 days", value = 7)
    private Integer periodInDays;
    @NotNull(message = "Start date should not be empty")
    private Timestamp startDate;
    @NotNull(message = "Status should not be empty!")
    private TaskStatus status;
    @NotBlank(message = "Task title should not be empty!")
    private String taskTitle;
    private String taskDetails;
    private Long userId;
    private String firstName;
    private String lastName;
}