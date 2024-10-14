package com.example.gymcrm.dto.update;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PasswordChangeDTO {

    @NotNull(message = "Username is required")
    @NotEmpty(message = "Username is required")
    private String username;

    @NotNull(message = "Old password is required")
    @NotEmpty(message = "Old password is required") 
    private String oldPassword;

    @NotNull(message = "New password is required")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{10}$", message = "Password must be 10 characters long and contain both letters and numbers")
    private String newPassword;
}
