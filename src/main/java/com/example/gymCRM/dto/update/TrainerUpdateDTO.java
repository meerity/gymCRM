package com.example.gymcrm.dto.update;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class TrainerUpdateDTO {

    @Pattern(regexp = "^[a-zA-Z]+$", message = "First name should contain only letters")
    private String firstName;

    @Pattern(regexp = "^[a-zA-Z]+$", message = "Last name should contain only letters")
    private String lastName;

    private String specialization;

    public boolean areAllFieldsEmpty() {
        return (firstName == null || firstName.isEmpty()) &&
               (lastName == null || lastName.isEmpty()) &&
            (specialization == null || specialization.isEmpty());
    }

}