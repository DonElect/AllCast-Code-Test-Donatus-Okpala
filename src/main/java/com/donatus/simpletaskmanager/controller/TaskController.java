package com.donatus.simpletaskmanager.controller;

import com.donatus.simpletaskmanager.dto.*;
import com.donatus.simpletaskmanager.services.TaskManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/task-mgmt")
@RequiredArgsConstructor
public class TaskController {
    private static final Logger log = LoggerFactory.getLogger(TaskController.class);
    private final TaskManagementService taskService;

    /**
     * Creates a new task and assigns it to a user.
     * @param taskRequest the request body containing task details.
     * @return ResponseEntity containing the task response.
     */
    @PostMapping("/tasks_assign")
    public ResponseEntity<ApiResponse<TaskResponse>> createAndAssignTask(@Valid @RequestBody TaskRequest taskRequest){
        return taskService.createNewTaskAndAssignToUser(taskRequest);
    }

    /**
     * Creates a new task without assigning it to a user.
     * @param taskRequest the request body containing task details.
     * @return ResponseEntity containing the task response.
     */
    @PostMapping("/tasks")
    public ResponseEntity<ApiResponse<TaskResponse>> createTaskOnly(@Valid @RequestBody TaskRequest taskRequest){
        return taskService.createNewTaskOnly(taskRequest);
    }

    /**
     * Retrieves tasks in a paginated manner.
     * @param pageNum the page number to retrieve.
     * @param pageSize the number of tasks per page.
     * @return ResponseEntity containing the paginated list of tasks.
     */
    @GetMapping("/tasks")
    public ResponseEntity<ApiResponse<PaginatedResponse<TaskResponse>>> getTaskInPages(@RequestParam("pageNum") int pageNum,
                                                                                       @RequestParam("pageSize") int pageSize){
        return taskService.getTasksByPage(pageNum, pageSize);
    }

    /**
     * Updates a task.
     * @param taskRequest the request body containing updated task details.
     * @param taskId the ID of the task to update.
     * @return ResponseEntity containing the task response.
     */
    @PutMapping("/tasks")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(@Valid @RequestBody TaskRequest taskRequest,
                                                                @RequestParam("taskId") Long taskId){
        log.info("Task request body: {}", taskRequest);
        return taskService.updateTask(taskRequest, taskId);
    }

    /**
     * Deletes a task by its ID.
     * @param taskId the ID of the task to delete.
     * @return ResponseEntity containing the task response.
     */
    @DeleteMapping("/tasks")
    public ResponseEntity<ApiResponse<TaskResponse>> DeleteTask(@RequestParam("taskId") Long taskId){
        return taskService.deleteTaskById(taskId);
    }

    /**
     * Assigns a task to a user.
     * @param taskRequest the request body containing task assignment details.
     * @return ResponseEntity containing the task response.
     */
    @PutMapping("/assign")
    public ResponseEntity<ApiResponse<TaskResponse>> assignTask(@Valid @RequestBody AssignTaskRequest taskRequest){
        return taskService.assignTaskToUser(taskRequest);
    }

    /**
     * Updates the status of a task.
     * @param status the new status of the task.
     * @param taskId the ID of the task to update.
     * @return ResponseEntity containing the task response.
     */
    @PutMapping("/task_status")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTaskStatus(@RequestParam("status") String status,
                                                                      @RequestParam("taskId") Long taskId){
        return taskService.updateTaskStatus(status, taskId);
    }

    /**
     * Retrieves paginated tasks for the logged-in user.
     * @param pageNum the page number to retrieve.
     * @param pageSize the number of tasks per page.
     * @return ResponseEntity containing the paginated list of tasks.
     */
    @GetMapping("/tasks/users")
    public ResponseEntity<ApiResponse<PaginatedResponse<TaskResponse>>> getPagedUserTasks(@RequestParam("pageNum") int pageNum,
                                                                                          @RequestParam("pageSize") int pageSize){
        return taskService.userTaskPaged(pageNum, pageSize);
    }
}
