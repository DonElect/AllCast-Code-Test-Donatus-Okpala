package com.donatus.simpletaskmanager.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignTaskRequest {
    @NotNull(message = "Task id cannot be null")
    @Min(message = "Task id can not be less that 1!", value = 1)
    private Long taskId;
    @NotNull(message = "User email cannot be null")
    private String email;
}
