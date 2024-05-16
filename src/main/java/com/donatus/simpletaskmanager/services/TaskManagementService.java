package com.donatus.simpletaskmanager.services;

import com.donatus.simpletaskmanager.dto.*;
import com.donatus.simpletaskmanager.exception.GenericException;
import com.donatus.simpletaskmanager.exception.UserNotFoundException;
import com.donatus.simpletaskmanager.models.TaskEntity;
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

import java.util.Collections;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskManagementService {
    private final TaskRepository taskRepo;
    private final UserRepository userRepo;
    private final ModelMapper mapper = new ModelMapper();
    ApiResponse<TaskResponse> response = new ApiResponse<>();
    ApiResponse<PaginatedResponse<TaskResponse>> paginatedApiResponse = new ApiResponse<>();

    public ResponseEntity<ApiResponse<TaskResponse>> createNewTaskAndAssignToUser(TaskRequest taskRequest) {
        if (taskRequest.getFirstName() == null || taskRequest.getLastName() == null) {
            throw new GenericException(HttpStatus.BAD_REQUEST, "First name and last name is required", "Input first and last name an try again.");
        }

        UserEntity user = userRepo.findByFirstNameIgnoreCaseAndLastNameIgnoreCase(taskRequest.getFirstName(), taskRequest.getLastName())
                .orElseThrow(() -> new UserNotFoundException("User does not exist!"));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntity admin = userRepo.findUserEntityByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User does not exist!"));

        try {
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

            TaskEntity savedTask = taskRepo.save(newTask);

            response.setCode("201");
            response.setDescription("Successful");
            TaskResponse taskResponse = mapper.map(savedTask, TaskResponse.class);

            taskResponse.setFirstName(user.getFirstName());
            taskResponse.setLastName(user.getLastName());
            taskResponse.setEmail(user.getEmail());
            response.setResponseData(taskResponse);

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception ex) {
            log.error("Failed to create task: ", ex);
            response.setCode("500");
            response.setDescription("Failed to create task");
            response.setResponseData(null);

            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ApiResponse<TaskResponse>> createNewTaskOnly(TaskRequest taskRequest) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntity admin = userRepo.findUserEntityByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User does not exist!"));

        try {
            TaskEntity newTask = TaskEntity.builder()
                    .taskTitle(taskRequest.getTaskTitle())
                    .taskDetails(taskRequest.getTaskDetails())
                    .periodInDays(taskRequest.getPeriodInDays())
                    .startDate(taskRequest.getStartDate())
                    .createdBy(admin.getEmail())
                    .status(taskRequest.getStatus())
                    .build();

            TaskEntity savedTask = taskRepo.save(newTask);

            response.setCode("201");
            response.setDescription("Successful");
            response.setResponseData(mapper.map(savedTask, TaskResponse.class));

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception ex) {
            log.error("Failed to create task: ", ex);
            response.setCode("500");
            response.setDescription("Failed to create task");
            response.setResponseData(null);

            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ApiResponse<PaginatedResponse<TaskResponse>>> getTasksByPage(int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize);

        Slice<TaskEntity> pagedTasks = taskRepo.pageAllTask(pageable);
        PaginatedResponse<TaskResponse> paginatedResponse = new PaginatedResponse<>();

        if (pagedTasks.isEmpty()) {
            paginatedResponse.setLast(true);
            paginatedApiResponse.setCode("200");
            paginatedApiResponse.setDescription("Successful");
            paginatedApiResponse.setResponseData(paginatedResponse);

            return new ResponseEntity<>(paginatedApiResponse, HttpStatus.OK);
        }
        paginatedResponse.setContent(pagedTasks.stream()
                .map(taskEntity -> {
                    TaskResponse taskResponse = mapper.map(taskEntity, TaskResponse.class);

                    taskResponse.setFirstName(taskEntity.getUser().getFirstName());
                    taskResponse.setLastName(taskEntity.getUser().getLastName());
                    taskResponse.setEmail(taskEntity.getUser().getEmail());

                    return taskResponse;
                })
                .toList());
        paginatedResponse.setPageNo(pagedTasks.getNumber());
        paginatedResponse.setPageSize(pagedTasks.getSize());
        paginatedResponse.setLast(pagedTasks.isLast());
        paginatedResponse.setTotalElement(pagedTasks.getNumberOfElements());

        paginatedApiResponse.setCode("200");
        paginatedApiResponse.setDescription("Successful");
        paginatedApiResponse.setResponseData(paginatedResponse);

        return new ResponseEntity<>(paginatedApiResponse, HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(TaskRequest taskRequest, Long taskId) {
        TaskEntity task = taskRepo.findTaskEntityById(taskId)
                .orElseThrow(() -> new GenericException(HttpStatus.BAD_REQUEST, "Invalid task id", "Check task id and try again"));

        UserEntity user = task.getUser();

        task.setPeriodInDays(taskRequest.getPeriodInDays());
        task.setStartDate(taskRequest.getStartDate());
        task.setStatus(taskRequest.getStatus());
        task.setTaskTitle(taskRequest.getTaskTitle());
        task.setTaskDetails(taskRequest.getTaskDetails());

        return getApiTaskResponse(user, task);
    }

    public ResponseEntity<ApiResponse<TaskResponse>> deleteTaskById(Long taskId){
        taskRepo.deleteById(taskId);

        response.setCode("202");
        response.setDescription("Successful");
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    public ResponseEntity<ApiResponse<TaskResponse>> assignTaskToUser(AssignTaskRequest taskRequest){
        UserEntity user = userRepo.findByFirstNameIgnoreCaseAndLastNameIgnoreCaseOrEmail(taskRequest.getFirstName(),
                        taskRequest.getLastName(), taskRequest.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User does not exist!"));

        TaskEntity task = taskRepo.findTaskEntityById(taskRequest.getTaskId())
                .orElseThrow(() -> new GenericException(HttpStatus.BAD_REQUEST, "Invalid task id", "Check task id and try again"));

        task.setUser(user);
        return getApiTaskResponse(user, task);
    }

    private ResponseEntity<ApiResponse<TaskResponse>> getApiTaskResponse(UserEntity user, TaskEntity task) {
        TaskEntity savedTask = taskRepo.save(task);

        response.setCode("200");
        response.setDescription("Successful");
        TaskResponse taskResponse = mapper.map(savedTask, TaskResponse.class);

        taskResponse.setFirstName(user.getFirstName());
        taskResponse.setLastName(user.getLastName());
        taskResponse.setEmail(user.getEmail());
        response.setResponseData(taskResponse);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
