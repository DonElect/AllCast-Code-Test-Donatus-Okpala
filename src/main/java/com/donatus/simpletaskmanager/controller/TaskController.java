package com.donatus.simpletaskmanager.controller;

import com.donatus.simpletaskmanager.dto.*;
import com.donatus.simpletaskmanager.models.TaskEntity;
import com.donatus.simpletaskmanager.services.TaskManagementService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/task-mgmt")
@RequiredArgsConstructor
public class TaskController {
    private static final Logger log = LoggerFactory.getLogger(TaskController.class);
    private final TaskManagementService taskService;

    @PostMapping("/tasks_assign")
    public ResponseEntity<ApiResponse<TaskResponse>> createAndAssignTask(@Valid @RequestBody TaskRequest taskRequest){
        return taskService.createNewTaskAndAssignToUser(taskRequest);
    }

    @PostMapping("/tasks")
    public ResponseEntity<ApiResponse<TaskResponse>> createTaskOnly(@Valid @RequestBody TaskRequest taskRequest){
        return taskService.createNewTaskOnly(taskRequest);
    }

    @GetMapping("/tasks")
    public ResponseEntity<ApiResponse<PaginatedResponse<TaskResponse>>> getTaskInPages(@RequestParam("pageNum") int pageNum,
                                                                                            @RequestParam("pageSize") int pageSize){
        return taskService.getTasksByPage(pageNum, pageSize);
    }

    @PutMapping("/tasks")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(@Valid @RequestBody TaskRequest taskRequest,
                                                                @RequestParam("taskId") Long taskId){
        log.info("Task request body: {}", taskRequest);
        return taskService.updateTask(taskRequest, taskId);
    }

    @DeleteMapping("/tasks")
    public ResponseEntity<ApiResponse<TaskResponse>> DeleteTask(@RequestParam("taskId") Long taskId){
        return taskService.deleteTaskById(taskId);
    }

    @PutMapping("/assign")
    public ResponseEntity<ApiResponse<TaskResponse>> assignTask(@Valid @RequestBody AssignTaskRequest taskRequest){
        return taskService.assignTaskToUser(taskRequest);
    }

    @PutMapping("/task_status")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTaskStatus(@RequestParam("status") String status,
            @RequestParam("taskId") Long taskId){
        return taskService.updateTaskStatus(status, taskId);
    }

    @GetMapping("/tasks/users")
    public ResponseEntity<ApiResponse<PaginatedResponse<TaskResponse>>> getPagedUserTasks(@RequestParam("pageNum") int pageNum,
                                                                                       @RequestParam("pageSize") int pageSize){
        return taskService.userTaskPaged(pageNum, pageSize);
    }
}
