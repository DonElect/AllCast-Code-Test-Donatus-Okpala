package com.donatus.simpletaskmanager.services;

import com.donatus.simpletaskmanager.dto.ApiResponse;
import com.donatus.simpletaskmanager.dto.TaskRequest;
import com.donatus.simpletaskmanager.exception.GenericException;
import com.donatus.simpletaskmanager.exception.UserNotFoundException;
import com.donatus.simpletaskmanager.models.TaskEntity;
import com.donatus.simpletaskmanager.models.UserEntity;
import com.donatus.simpletaskmanager.repository.TaskEntityRepository;
import com.donatus.simpletaskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskManagementService {
    private final TaskEntityRepository taskRepo;
    private final UserRepository userRepo;

    public ResponseEntity<ApiResponse<TaskEntity>> createNewTaskAndAssignToUser(TaskRequest taskRequest){
        if(taskRequest.getFirstName() == null || taskRequest.getLastName() == null){
            throw new GenericException(HttpStatus.BAD_REQUEST, "First name and last name is required", "Input first and last name an try again.");
        }

        UserEntity user = userRepo.findByFirstNameIgnoreCaseAndLastNameIgnoreCase(taskRequest.getFirstName(), taskRequest.getLastName())
                .orElseThrow(()-> new UserNotFoundException("User does not exist!"));

        ApiResponse<TaskEntity> response = new ApiResponse<>();
        try{
            TaskEntity newTask = TaskEntity.builder()
                    .taskTitle(taskRequest.getTaskTitle())
                    .taskDetails(taskRequest.getTaskDetails())
                    .periodInDays(taskRequest.getPeriodInDays())
                    .startDate(taskRequest.getStartDate())
                    .status(taskRequest.getStatus())
                    .user(user)
                    .build();

            TaskEntity savedTask =  taskRepo.save(newTask);

            savedTask.setUser(null);

            response.setCode("201");
            response.setDescription("Successful");
            response.setResponseData(savedTask);

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }catch (Exception ex){
            log.error("Failed to create task: ", ex);
            response.setCode("500");
            response.setDescription("Failed to create task");
            response.setResponseData(null);

            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
