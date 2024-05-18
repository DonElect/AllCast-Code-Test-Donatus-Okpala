package com.donatus.simpletaskmanager.dto;

import com.donatus.simpletaskmanager.models.Roles;
import lombok.*;

/**
 * DTO for {@link com.donatus.simpletaskmanager.models.UserEntity}
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserResponse {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    private Roles role;
    private AuthResponse authResponse;
}