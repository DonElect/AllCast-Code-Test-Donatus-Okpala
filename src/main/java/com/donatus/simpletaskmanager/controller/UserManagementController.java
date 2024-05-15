package com.donatus.simpletaskmanager.controller;

import com.donatus.simpletaskmanager.dto.ApiResponse;
import com.donatus.simpletaskmanager.dto.LoginRequest;
import com.donatus.simpletaskmanager.dto.UserRequest;
import com.donatus.simpletaskmanager.dto.UserResponse;
import com.donatus.simpletaskmanager.services.UserManagementService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/v1/user-mgmt")
@RequiredArgsConstructor
public class UserManagementController {
    private final UserManagementService userManagementService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<String>> signupNewUser(@Valid @RequestBody UserRequest userRequest,
                                                             HttpServletRequest servletRequest){
        return userManagementService.registerNewUser(userRequest, servletRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponse>> loginUser(@Valid @RequestBody LoginRequest loginRequest,
                                                               HttpServletRequest servletRequest){
        return userManagementService.loginUser(loginRequest, servletRequest);
    }
}
