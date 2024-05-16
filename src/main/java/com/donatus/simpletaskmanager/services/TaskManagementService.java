package com.donatus.simpletaskmanager.services;

import com.donatus.simpletaskmanager.dto.ApiResponse;
import com.donatus.simpletaskmanager.dto.PaginatedResponse;
import com.donatus.simpletaskmanager.dto.TaskRequest;
import com.donatus.simpletaskmanager.dto.TaskResponse;
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
    ApiResponse<TaskEntity> response = new ApiResponse<>();
    ApiResponse<PaginatedResponse<TaskResponse>> paginatedApiResponse = new ApiResponse<>();

    public ResponseEntity<ApiResponse<TaskEntity>> createNewTaskAndAssignToUser(TaskRequest taskRequest) {
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

            savedTask.setUser(null);

            response.setCode("201");
            response.setDescription("Successful");
            response.setResponseData(savedTask);

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception ex) {
            log.error("Failed to create task: ", ex);
            response.setCode("500");
            response.setDescription("Failed to create task");
            response.setResponseData(null);

            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ApiResponse<TaskEntity>> createNewTaskOnly(TaskRequest taskRequest) {
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
            response.setResponseData(savedTask);

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
                        .map(taskEntity -> mapper.map(taskEntity, TaskResponse.class))
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
}
