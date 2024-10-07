package com.example.gymcrm.dto.update;

import java.time.LocalDate;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class TraineeUpdateDTO {
    
    @Pattern(regexp = "^[a-zA-Z]+$", message = "First name should contain only letters")
    private String firstName;

    @Pattern(regexp = "^[a-zA-Z]+$", message = "Last name should contain only letters")
    private String lastName;

    private String address;

    private LocalDate dateOfBirth;

    public boolean areAllFieldsEmpty() {
        return (firstName == null || firstName.isEmpty()) &&
               (lastName == null || lastName.isEmpty()) &&
               (address == null || address.isEmpty()) &&
               dateOfBirth == null;
    }
}
