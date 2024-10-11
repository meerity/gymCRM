package com.example.gymcrm.dto.update;

import java.time.LocalDate;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class TraineeUpdateDTO {
    
    @NotEmpty(message = "First name cannot be empty")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "First name should contain only letters")
    private String firstName;

    @NotEmpty(message = "Last name cannot be empty")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Last name should contain only letters")
    private String lastName;

    private LocalDate dateOfBirth;

    private String address;

    @NotNull(message = "Active status cannot be null")
    private Boolean isActive;

    public boolean areAllFieldsEmpty() {
        return (firstName == null || firstName.isEmpty()) &&
               (lastName == null || lastName.isEmpty()) &&
               (address == null || address.isEmpty()) &&
               dateOfBirth == null &&
               isActive == null;
    }
}
