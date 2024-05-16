package com.donatus.simpletaskmanager.controller;

import com.donatus.simpletaskmanager.dto.ApiResponse;
import com.donatus.simpletaskmanager.dto.PaginatedResponse;
import com.donatus.simpletaskmanager.dto.TaskRequest;
import com.donatus.simpletaskmanager.dto.TaskResponse;
import com.donatus.simpletaskmanager.models.TaskEntity;
import com.donatus.simpletaskmanager.services.TaskManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/task-mgmt")
@RequiredArgsConstructor
public class TaskController {
    private final TaskManagementService taskService;

    @PostMapping("/tasks_assign")
    public ResponseEntity<ApiResponse<TaskEntity>> createAndAssignTask(@Valid @RequestBody TaskRequest taskRequest){
        return taskService.createNewTaskAndAssignToUser(taskRequest);
    }

    @PostMapping("/tasks")
    public ResponseEntity<ApiResponse<TaskEntity>> createTaskOnly(@Valid @RequestBody TaskRequest taskRequest){
        return taskService.createNewTaskOnly(taskRequest);
    }

    @GetMapping("/tasks")
    public ResponseEntity<ApiResponse<PaginatedResponse<TaskResponse>>> createAndAssignTask(@RequestParam("pageNum") int pageNum,
                                                                                            @RequestParam("pageSize") int pageSize){
        return taskService.getTasksByPage(pageNum, pageSize);
    }
}
