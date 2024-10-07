package com.example.gymcrm.dto.response;

import java.time.LocalDate;
import java.util.Set;
import lombok.Data;

@Data
public class TraineeResponse {

    private String firstName;
    private String lastName;
    private String username;
    private boolean active;
    private LocalDate dateOfBirth;
    private String address;
    private Set<String> trainerUsernames;
    private Set<String> trainingNames;

}
