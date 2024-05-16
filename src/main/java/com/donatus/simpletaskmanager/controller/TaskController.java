package com.donatus.simpletaskmanager.controller;

import com.donatus.simpletaskmanager.dto.ApiResponse;
import com.donatus.simpletaskmanager.dto.TaskRequest;
import com.donatus.simpletaskmanager.models.TaskEntity;
import com.donatus.simpletaskmanager.services.TaskManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/task-mgmt/tasks")
@RequiredArgsConstructor
public class TaskController {
//    String email = SecurityContextHolder.getContext().getAuthentication().getName();
    private final TaskManagementService taskService;

    @PostMapping("")
    public ResponseEntity<ApiResponse<TaskEntity>> createAndAssignTask(@Valid @RequestBody TaskRequest taskRequest){
        return taskService.createNewTaskAndAssignToUser(taskRequest);
    }

}
