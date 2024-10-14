package com.example.gymcrm.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class TrainerDTO {

    @NotBlank(message = "First name cannot be empty")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "First name should contain only letters")
    private String firstName;

    @NotBlank(message = "Last name cannot be empty")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Last name should contain only letters")
    private String lastName;

    @NotBlank(message = "Specialization cannot be empty")
    private String specialization;

}
