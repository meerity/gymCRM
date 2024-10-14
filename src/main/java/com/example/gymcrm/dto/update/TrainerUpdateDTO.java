package com.example.gymcrm.dto.update;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class TrainerUpdateDTO {

    @NotEmpty(message = "First name cannot be empty")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "First name should contain only letters")
    private String firstName;

    @NotEmpty(message = "Last name cannot be empty")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Last name should contain only letters")
    private String lastName;

    @NotEmpty(message = "Specialization is required")
    private String specialization;

    @NotNull(message = "Active status is required")
    private Boolean active;

    public boolean areAllFieldsEmpty() {
        return (firstName == null || firstName.isEmpty()) &&
               (lastName == null || lastName.isEmpty()) &&
               (specialization == null || specialization.isEmpty()) &&
               (active == null);
    }

}