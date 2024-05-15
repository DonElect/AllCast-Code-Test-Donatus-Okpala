package com.donatus.simpletaskmanager.dto;

import com.donatus.simpletaskmanager.models.Gender;
import lombok.*;

/**
 * DTO for {@link com.donatus.simpletaskmanager.models.UserEntity}
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
    private String address;
    private Gender gender;
}