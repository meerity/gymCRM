package com.example.gymcrm.dto.update;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PasswordChangeDTO {
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{10}$", message = "Password must be 10 characters long and contain both letters and numbers")
    private String password;
}
