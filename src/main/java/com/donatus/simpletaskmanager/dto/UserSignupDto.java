package com.donatus.simpletaskmanager.dto;

import com.donatus.simpletaskmanager.models.UserEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO for {@link UserEntity}
 */
@Getter
@Setter
@ToString
public class UserSignupDto {
    @NotBlank(message = "First name can not be blank!")
    private String firstName;
    @NotBlank(message = "Last name can not be blank!")
    private String lastName;
    @Email(message = "Email is invalid!")
    @NotBlank(message = "Email can not be blank!")
    private String email;
    @NotBlank(message = "Password can not be blank!")
    private String password;
    @NotBlank(message = "Confirm password can not be blank!")
    private String confirmPassword;
    @NotBlank(message = "Phone number can not be blank!")
    private String phoneNumber;
    @NotBlank(message = "Address can not be blank!")
    private String address;
    @NotBlank(message = "Gender can not be blank!")
    private String gender;
}