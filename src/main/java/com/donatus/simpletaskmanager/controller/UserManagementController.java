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

    /**
     * Registers a new user.
     * @param signupDto the request body containing user registration details.
     * @param servletRequest the HTTP request.
     * @return ResponseEntity containing the registration response.
     */
    @PostMapping("/user/signup")
    public ResponseEntity<ApiResponse<String>> signupNewUser(@Valid @RequestBody UserSignupDto signupDto,
                                                             HttpServletRequest servletRequest){
        return userManagementService.registerNewUser(signupDto, servletRequest);
    }

    /**
     * Authenticates a user and logs them in.
     * @param loginRequest the request body containing login details.
     * @param servletRequest the HTTP request.
     * @return ResponseEntity containing the login response.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponse>> loginUser(@Valid @RequestBody LoginRequest loginRequest,
                                                               HttpServletRequest servletRequest){
        return userManagementService.loginUser(loginRequest, servletRequest);
    }

    /**
     * Registers a new admin user.
     * @param signupDto the request body containing admin registration details.
     * @param servletRequest the HTTP request.
     * @return ResponseEntity containing the registration response.
     */
    @PostMapping("/admin/signup")
    public ResponseEntity<ApiResponse<String>> signupNewAdmin(@Valid @RequestBody UserSignupDto signupDto,
                                                              HttpServletRequest servletRequest){
        return userManagementService.registerNewUser(signupDto, servletRequest);
    }

    /**
     * Retrieves users in a paginated manner.
     * @param pageNum the page number to retrieve.
     * @param pageSize the number of users per page.
     * @return ResponseEntity containing the paginated list of users.
     */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<PaginatedResponse<UserResponse>>> getUserPaged(@RequestParam("pageNum") int pageNum,
                                                                                     @RequestParam("pageSize") int pageSize){
        return userManagementService.getUsersPaged(pageNum, pageSize);
    }
}
