package com.donatus.simpletaskmanager.services;

import com.donatus.simpletaskmanager.dto.*;
import com.donatus.simpletaskmanager.exception.GenericException;
import com.donatus.simpletaskmanager.exception.UserNotFoundException;
import com.donatus.simpletaskmanager.models.TaskEntity;
import com.donatus.simpletaskmanager.models.TaskStatus;
import com.donatus.simpletaskmanager.models.UserEntity;
import com.donatus.simpletaskmanager.repository.TaskRepository;
import com.donatus.simpletaskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service // Marks this class as a Spring service
@Slf4j // Enables logging in this class
@RequiredArgsConstructor // Generates a constructor with required arguments (final fields)
public class TaskManagementService {
    private final TaskRepository taskRepo; // Repository for managing tasks
    private final UserRepository userRepo; // Repository for managing users
    private final ModelMapper mapper = new ModelMapper(); // Mapper for converting entities to DTOs
    private ApiResponse<TaskResponse> response; // Response object for single task operations

    /**
     * Creates a new task and assigns it to a user.
     * @param taskRequest the request object containing task details and user info.
     * @return ResponseEntity containing the result of the task creation.
     */
    public ResponseEntity<ApiResponse<TaskResponse>> createNewTaskAndAssignToUser(TaskRequest taskRequest) {
        // Validate task request
        if (taskRequest.getFirstName() == null || taskRequest.getLastName() == null) {
            throw new GenericException(HttpStatus.BAD_REQUEST, "First name and last name are required", "Input first and last name and try again.");
        }

        // Find user by first and last name
        UserEntity user = userRepo.findByFirstNameIgnoreCaseAndLastNameIgnoreCase(taskRequest.getFirstName(), taskRequest.getLastName())
                .orElseThrow(() -> new UserNotFoundException("User does not exist!"));

        // Get the email of the currently authenticated user
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        // Find the admin user by email
        UserEntity admin = userRepo.findUserEntityByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User does not exist!"));

        response = new ApiResponse<>();

        try {
            // Build the new task entity
            TaskEntity newTask = TaskEntity.builder()
                    .taskTitle(taskRequest.getTaskTitle())
                    .taskDetails(taskRequest.getTaskDetails())
                    .periodInDays(taskRequest.getPeriodInDays())
                    .startDate(taskRequest.getStartDate())
                    .status(taskRequest.getStatus())
                    .createdBy(admin.getEmail())
                    .assignBy(admin.getEmail())
                    .user(user)
                    .build();

            // Save the new task to the repository
            TaskEntity savedTask = taskRepo.save(newTask);

            // Prepare and return the response
            response.setCode("201");
            response.setDescription("Successful");
            TaskResponse taskResponse = mapper.map(savedTask, TaskResponse.class);
            taskResponse.setFirstName(user.getFirstName());
            taskResponse.setLastName(user.getLastName());
            taskResponse.setEmail(user.getEmail());
            response.setResponseData(taskResponse);

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception ex) {
            // Log the error and prepare the error response
            log.error("Failed to create task: ", ex);
            response.setCode("500");
            response.setDescription("Failed to create task");
            response.setResponseData(null);

            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Creates a new task without assigning it to a specific user.
     * @param taskRequest the request object containing task details.
     * @return ResponseEntity containing the result of the task creation.
     */
    public ResponseEntity<ApiResponse<TaskResponse>> createNewTaskOnly(TaskRequest taskRequest) {
        // Get the email of the currently authenticated user
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        // Find the admin user by email
        UserEntity admin = userRepo.findUserEntityByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User does not exist!"));

        response = new ApiResponse<>();

        try {
            // Build the new task entity
            TaskEntity newTask = TaskEntity.builder()
                    .taskTitle(taskRequest.getTaskTitle())
                    .taskDetails(taskRequest.getTaskDetails())
                    .periodInDays(taskRequest.getPeriodInDays())
                    .startDate(taskRequest.getStartDate())
                    .createdBy(admin.getEmail())
                    .status(taskRequest.getStatus())
                    .build();

            // Save the new task to the repository
            TaskEntity savedTask = taskRepo.save(newTask);

            // Prepare and return the response
            response.setCode("201");
            response.setDescription("Successful");
            response.setResponseData(mapper.map(savedTask, TaskResponse.class));

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception ex) {
            // Log the error and prepare the error response
            log.error("Failed to create task: ", ex);
            response.setCode("500");
            response.setDescription("Failed to create task");
            response.setResponseData(null);

            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves tasks in a paginated manner.
     * @param pageNum the page number to retrieve.
     * @param pageSize the number of tasks per page.
     * @return ResponseEntity containing the paginated list of tasks.
     */
    public ResponseEntity<ApiResponse<PaginatedResponse<TaskResponse>>> getTasksByPage(int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize);

        // Get paginated tasks from the repository
        Slice<TaskEntity> pagedTasks = taskRepo.pageAllTask(pageable);
        return getApiResponsePaged(pagedTasks);
    }

    /**
     * Updates an existing task.
     * @param taskRequest the request object containing updated task details.
     * @param taskId the ID of the task to update.
     * @return ResponseEntity containing the result of the task update.
     */
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(TaskRequest taskRequest, Long taskId) {
        // Find the task by ID
        TaskEntity task = taskRepo.findTaskEntityById(taskId)
                .orElseThrow(() -> new GenericException(HttpStatus.BAD_REQUEST, "Invalid task id", "Check task id and try again"));

        UserEntity user = task.getUser();

        // Update the task entity with new details
        task.setPeriodInDays(taskRequest.getPeriodInDays());
        task.setStartDate(taskRequest.getStartDate());
        task.setStatus(taskRequest.getStatus());
        task.setTaskTitle(taskRequest.getTaskTitle());
        task.setTaskDetails(taskRequest.getTaskDetails());

        return getApiTaskResponse(user, task);
    }

    /**
     * Deletes a task by its ID.
     * @param taskId the ID of the task to delete.
     * @return ResponseEntity indicating the result of the delete operation.
     */
    public ResponseEntity<ApiResponse<TaskResponse>> deleteTaskById(Long taskId){
        // Delete the task from the repository
        taskRepo.deleteById(taskId);

        response = new ApiResponse<>();

        // Prepare and return the response
        response.setCode("202");
        response.setDescription("Successful");
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    /**
     * Assigns an existing task to a user.
     * @param taskRequest the request object containing the task ID and user email.
     * @return ResponseEntity containing the result of the task assignment.
     */
    public ResponseEntity<ApiResponse<TaskResponse>> assignTaskToUser(AssignTaskRequest taskRequest){
        // Find the user by email
        UserEntity user = userRepo.findUserEntityByEmail(taskRequest.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User does not exist!"));

        // Find the task by ID
        TaskEntity task = taskRepo.findTaskEntityById(taskRequest.getTaskId())
                .orElseThrow(() -> new GenericException(HttpStatus.BAD_REQUEST, "Invalid task id", "Check task id and try again"));

        // Assign the task to the user
        task.setUser(user);
        return getApiTaskResponse(user, task);
    }

    /**
     * Helper method to save a task and prepare the response.
     * @param user the user to whom the task is assigned (optional).
     * @param task the task entity to save.
     * @return ResponseEntity containing the result of the task save operation.
     */
    private ResponseEntity<ApiResponse<TaskResponse>> getApiTaskResponse(UserEntity user, TaskEntity task) {
        // Save the task to the repository
        TaskEntity savedTask = taskRepo.save(task);

        response = new ApiResponse<>();

        // Prepare and return the response
        response.setCode("200");
        response.setDescription("Successful");
        TaskResponse taskResponse = mapper.map(savedTask, TaskResponse.class);

        if (user != null) {
            taskResponse.setFirstName(user.getFirstName());
            taskResponse.setLastName(user.getLastName());
            taskResponse.setEmail(user.getEmail());
        }

        response.setResponseData(taskResponse);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Updates the status of an existing task.
     * @param status the new status of the task.
     * @param taskId the ID of the task to update.
     * @return ResponseEntity containing the result of the task status update.
     */
    public ResponseEntity<ApiResponse<TaskResponse>> updateTaskStatus(String status, Long taskId){
        // Find the task by ID
        TaskEntity task = taskRepo.findTaskEntityById(taskId)
                .orElseThrow(() -> new GenericException(HttpStatus.BAD_REQUEST, "Invalid task id", "Check task id and try again"));

        // Update the task status
        task.setStatus(TaskStatus.valueOf(status.toUpperCase()));

        // Save the updated task to the repository
        TaskEntity updatedTask = taskRepo.save(task);

        return getApiTaskResponse(updatedTask.getUser(), updatedTask);
    }

    /**
     * Retrieves tasks assigned to the currently authenticated user in a paginated manner.
     * @param pageNum the page number to retrieve.
     * @param pageSize the number of tasks per page.
     * @return ResponseEntity containing the paginated list of tasks.
     */
    public ResponseEntity<ApiResponse<PaginatedResponse<TaskResponse>>> userTaskPaged(int pageNum, int pageSize) {
        // Get the email of the currently authenticated user
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        // Find the user by email
        UserEntity user = userRepo.findUserEntityByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User does not exist!"));

        Pageable pageable = PageRequest.of(pageNum, pageSize);

        // Get paginated tasks assigned to the user from the repository
        Slice<TaskEntity> pagedTasks = taskRepo.findByUserId(user.getId(), pageable);
        return getApiResponsePaged(pagedTasks);
    }

    /**
     * Helper method to prepare a paginated response.
     * @param pagedTasks the slice of task entities.
     * @return ResponseEntity containing the paginated list of tasks.
     */
    private ResponseEntity<ApiResponse<PaginatedResponse<TaskResponse>>> getApiResponsePaged(Slice<TaskEntity> pagedTasks) {
        PaginatedResponse<TaskResponse> paginatedResponse = new PaginatedResponse<>();

        // Response object for paginated task results
        ApiResponse<PaginatedResponse<TaskResponse>> paginatedApiResponse = new ApiResponse<>();

        if (pagedTasks.isEmpty()) {
            // Prepare and return the response for empty result set
            paginatedResponse.setLast(true);
            paginatedApiResponse.setCode("200");
            paginatedApiResponse.setDescription("Successful");
            paginatedApiResponse.setResponseData(paginatedResponse);

            return new ResponseEntity<>(paginatedApiResponse, HttpStatus.OK);
        }

        // Convert task entities to task responses
        paginatedResponse.setContent(pagedTasks.stream()
                .map(taskEntity -> {
                    TaskResponse taskResponse = mapper.map(taskEntity, TaskResponse.class);

                    if (taskEntity.getUser() != null) {
                        taskResponse.setFirstName(taskEntity.getUser().getFirstName());
                        taskResponse.setLastName(taskEntity.getUser().getLastName());
                        taskResponse.setEmail(taskEntity.getUser().getEmail());
                    }

                    return taskResponse;
                })
                .toList());
        paginatedResponse.setPageNum(pagedTasks.getNumber());
        paginatedResponse.setPageSize(pagedTasks.getSize());
        paginatedResponse.setLast(pagedTasks.isLast());
        paginatedResponse.setTotalElement(pagedTasks.getNumberOfElements());

        // Prepare and return the response
        paginatedApiResponse.setCode("200");
        paginatedApiResponse.setDescription("Successful");
        paginatedApiResponse.setResponseData(paginatedResponse);

        return new ResponseEntity<>(paginatedApiResponse, HttpStatus.OK);
    }
}
