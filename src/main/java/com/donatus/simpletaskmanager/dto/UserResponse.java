package com.donatus.simpletaskmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for {@link com.donatus.simpletaskmanager.models.UserEntity}
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserResponse {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
}