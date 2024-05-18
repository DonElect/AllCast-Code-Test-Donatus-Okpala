package com.donatus.simpletaskmanager.services;

import com.donatus.simpletaskmanager.dto.*;
import com.donatus.simpletaskmanager.exception.*;
import com.donatus.simpletaskmanager.models.Gender;
import com.donatus.simpletaskmanager.models.Roles;
import com.donatus.simpletaskmanager.models.TaskEntity;
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
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final JWTGenerator jwtGenerator;

    public ResponseEntity<ApiResponse<String>> registerNewUser(UserSignupDto userRequest, HttpServletRequest request) {
        if (userRepo.existsByEmail(userRequest.getEmail().strip())) {
            throw new DuplicateEmailException("Email already exist!");
        }

        if (!userRequest.getPassword().equals(userRequest.getConfirmPassword())) {
            throw new PasswordMismatchException("Password mismatch.");
        }

        ApiResponse<String> response = new ApiResponse<>();

        try {
            String path = request.getServletPath();

            UserEntity user = UserEntity.builder()
                    .firstName(userRequest.getFirstName())
                    .lastName(userRequest.getLastName())
                    .email(userRequest.getEmail().strip())
                    .password(encoder.encode(userRequest.getPassword()))
                    .confirmPassword(userRequest.getConfirmPassword())
                    .phoneNumber(userRequest.getPhoneNumber())
                    .address(userRequest.getAddress())
                    .gender(Gender.valueOf(userRequest.getGender().toUpperCase()))
                    .roles(
                            path.contains("/user/") ? Roles.USER : Roles.ADMIN
                    )
                    .build();
            userRepo.save(user);

            log.info("Path is: {}", path);
            log.info("User with email: {} registered.", user.getEmail());
            response.setCode("200");
            response.setDescription("Successful");
            response.setResponseData(null);

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception ex) {
            response.setCode("500");
            response.setDescription("An error occurred while registering user.");
            response.setResponseData(null);

            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ApiResponse<UserResponse>> loginUser(LoginRequest loginRequest, HttpServletRequest request) {
        String path = request.getServletPath();

        UserEntity user = userRepo.findUserEntityByEmail(loginRequest.getEmail().toLowerCase())
                .orElseThrow(() -> new UserNotFoundException("Invalid Email address."));

        if (!path.contains("/"+String.valueOf(user.getRoles()).toLowerCase()+"/")) {
            throw new UnauthorizedException("You are not authorized access this page");
        }


        if (!encoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Invalid password!");
        }

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail().strip(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        ApiResponse<UserResponse> response = new ApiResponse<>();

        String token = jwtGenerator.generateToken(authentication, 120L);
        String freshToken = jwtGenerator.generateToken(authentication, 1440L);
        AuthResponse authResponse = new AuthResponse(token, freshToken);

        UserResponse userResponse = UserResponse.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .address(user.getAddress())
                .phoneNumber(user.getPhoneNumber())
                .authResponse(authResponse)
                .build();

        response.setCode("200");
        response.setDescription("Successful");
        response.setResponseData(userResponse);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse<PaginatedResponse<UserResponse>>> getUsersPaged(int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize);

        Slice<UserEntity> pagedUser = userRepo.findAllByRoles(Roles.USER, pageable);
        PaginatedResponse<UserResponse> paginatedResponse = new PaginatedResponse<>();
        ApiResponse<PaginatedResponse<UserResponse>> paginatedApiResponse = new ApiResponse<>();

        if (pagedUser.isEmpty()) {
            paginatedResponse.setLast(true);
            paginatedApiResponse.setCode("200");
            paginatedApiResponse.setDescription("Successful");
            paginatedApiResponse.setResponseData(paginatedResponse);

            return new ResponseEntity<>(paginatedApiResponse, HttpStatus.OK);
        }
        paginatedResponse.setContent(pagedUser.stream()
                .map(userEntity ->
                    UserResponse.builder()
                            .firstName(userEntity.getFirstName())
                            .lastName(userEntity.getLastName())
                            .email(userEntity.getEmail())
                            .build()
                )
                .toList());
        paginatedResponse.setPageNum(pagedUser.getNumber());
        paginatedResponse.setPageSize(pagedUser.getSize());
        paginatedResponse.setLast(pagedUser.isLast());
        paginatedResponse.setTotalElement(pagedUser.getNumberOfElements());

        paginatedApiResponse.setCode("200");
        paginatedApiResponse.setDescription("Successful");
        paginatedApiResponse.setResponseData(paginatedResponse);

        return new ResponseEntity<>(paginatedApiResponse, HttpStatus.OK);
    }
}
