package com.donatus.simpletaskmanager.controller;

import com.donatus.simpletaskmanager.dto.*;
import com.donatus.simpletaskmanager.services.UserManagementService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user-mgmt")
@RequiredArgsConstructor
public class UserManagementController {
    private final UserManagementService userManagementService;

    @PostMapping("/user/signup")
    public ResponseEntity<ApiResponse<String>> signupNewUser(@Valid @RequestBody UserSignupDto signupDto,
                                                             HttpServletRequest servletRequest){
        return userManagementService.registerNewUser(signupDto, servletRequest);
    }

    @PostMapping("/user/login")
    public ResponseEntity<ApiResponse<UserResponse>> loginUser(@Valid @RequestBody LoginRequest loginRequest,
                                                               HttpServletRequest servletRequest){
        return userManagementService.loginUser(loginRequest, servletRequest);
    }

    @PostMapping("/admin/signup")
    public ResponseEntity<ApiResponse<String>> signupNewAmin(@Valid @RequestBody UserSignupDto signupDto,
                                                             HttpServletRequest servletRequest){
        return userManagementService.registerNewUser(signupDto, servletRequest);
    }

    @PostMapping("/admin/login")
    public ResponseEntity<ApiResponse<UserResponse>> loginAdmin(@Valid @RequestBody LoginRequest loginRequest,
                                                               HttpServletRequest servletRequest){
        return userManagementService.loginUser(loginRequest, servletRequest);
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<PaginatedResponse<UserResponse>>> getUserPaged(@RequestParam("pageNum") int pageNum,
                                                                                       @RequestParam("pageSize") int pageSize){
        return userManagementService.getUsersPaged(pageNum, pageSize);
    }
}
