package com.donatus.simpletaskmanager.services;

import com.donatus.simpletaskmanager.dto.*;
import com.donatus.simpletaskmanager.exception.*;
import com.donatus.simpletaskmanager.models.Gender;
import com.donatus.simpletaskmanager.models.Roles;
import com.donatus.simpletaskmanager.models.UserEntity;
import com.donatus.simpletaskmanager.repository.UserRepository;
import com.donatus.simpletaskmanager.security.JWTGenerator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserManagementService {
    private final UserRepository userRepo; // Repository for managing users
    private final PasswordEncoder encoder; // Encoder for hashing passwords
    private final AuthenticationManager authenticationManager; // Manager for authenticating users
    private final JWTGenerator jwtGenerator; // Generator for JWT tokens

    /**
     * Registers a new user.
     * @param userRequest the DTO containing user signup details.
     * @param request the HTTP request.
     * @return ResponseEntity containing the result of the user registration.
     */
    public ResponseEntity<ApiResponse<String>> registerNewUser(UserSignupDto userRequest, HttpServletRequest request) {
        // Check if email already exists
        if (userRepo.existsByEmail(userRequest.getEmail().strip())) {
            throw new DuplicateEmailException("Email already exist!");
        }

        // Check if passwords match
        if (!userRequest.getPassword().equals(userRequest.getConfirmPassword())) {
            throw new PasswordMismatchException("Password mismatch.");
        }

        ApiResponse<String> response = new ApiResponse<>();

        try {
            String path = request.getServletPath(); // Get the request path

            // Build the new user entity
            UserEntity user = UserEntity.builder()
                    .firstName(userRequest.getFirstName())
                    .lastName(userRequest.getLastName())
                    .email(userRequest.getEmail().strip())
                    .password(encoder.encode(userRequest.getPassword()))
                    .confirmPassword(userRequest.getConfirmPassword())
                    .phoneNumber(userRequest.getPhoneNumber())
                    .address(userRequest.getAddress())
                    .gender(Gender.valueOf(userRequest.getGender().toUpperCase()))
                    .roles(path.contains("/user/") ? Roles.USER : Roles.ADMIN) // Set roles based on the path
                    .build();
            userRepo.save(user); // Save the new user to the repository

            log.info("Path is: {}", path);
            log.info("User with email: {} registered.", user.getEmail());
            response.setCode("200");
            response.setDescription("Successful");
            response.setResponseData(null);

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception ex) {
            // Log the error and prepare the error response
            response.setCode("500");
            response.setDescription("An error occurred while registering user.");
            response.setResponseData(null);

            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Logs in a user.
     * @param loginRequest the DTO containing login details.
     * @param request the HTTP request.
     * @return ResponseEntity containing the result of the login operation.
     */
    public ResponseEntity<ApiResponse<UserResponse>> loginUser(LoginRequest loginRequest, HttpServletRequest request) {
        String path = request.getServletPath(); // Get the request path

        // Find user by email
        UserEntity user = userRepo.findUserEntityByEmail(loginRequest.getEmail().toLowerCase())
                .orElseThrow(() -> new UserNotFoundException("Invalid Email address."));

        // Check if the password matches
        if (!encoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Invalid password!");
        }

        // Authenticate the user
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail().strip(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ApiResponse<UserResponse> response = new ApiResponse<>();

        // Generate JWT tokens
        String token = jwtGenerator.generateToken(authentication, 120L);
        String freshToken = jwtGenerator.generateToken(authentication, 1440L);
        AuthResponse authResponse = new AuthResponse(token, freshToken);

        // Build the user response
        UserResponse userResponse = UserResponse.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .address(user.getAddress())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRoles())
                .authResponse(authResponse)
                .build();

        response.setCode("200");
        response.setDescription("Successful");
        response.setResponseData(userResponse);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves users in a paginated manner.
     * @param pageNum the page number to retrieve.
     * @param pageSize the number of users per page.
     * @return ResponseEntity containing the paginated list of users.
     */
    public ResponseEntity<ApiResponse<PaginatedResponse<UserResponse>>> getUsersPaged(int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize);

        // Get paginated users with role USER from the repository
        Slice<UserEntity> pagedUser = userRepo.findAllByRoles(Roles.USER, pageable);
        PaginatedResponse<UserResponse> pagedUserResponse = new PaginatedResponse<>();
        ApiResponse<PaginatedResponse<UserResponse>> paginatedApiResponse = new ApiResponse<>();

        if (pagedUser.isEmpty()) {
            // Prepare and return the response for empty result set
            pagedUserResponse.setLast(true);
            paginatedApiResponse.setCode("200");
            paginatedApiResponse.setDescription("Successful");
            paginatedApiResponse.setResponseData(pagedUserResponse);

            return new ResponseEntity<>(paginatedApiResponse, HttpStatus.OK);
        }

        // Convert user entities to user responses
        pagedUserResponse.setContent(pagedUser.stream()
                .map(userEntity -> UserResponse.builder()
                        .firstName(userEntity.getFirstName())
                        .lastName(userEntity.getLastName())
                        .email(userEntity.getEmail())
                        .build())
                .toList());
        pagedUserResponse.setPageNum(pagedUser.getNumber());
        pagedUserResponse.setPageSize(pagedUser.getSize());
        pagedUserResponse.setLast(pagedUser.isLast());
        pagedUserResponse.setTotalElement(pagedUser.getNumberOfElements());

        paginatedApiResponse.setCode("200");
        paginatedApiResponse.setDescription("Successful");
        paginatedApiResponse.setResponseData(pagedUserResponse);

        return new ResponseEntity<>(paginatedApiResponse, HttpStatus.OK);
    }
}
