package com.example.gymcrm.dto.response;

import java.util.Set;
import lombok.Data;

@Data
public class TrainerResponse {
    private String firstName;
    private String lastName;
    private String username;
    private boolean active;
    private String specialization;
    private Set<String> traineeUsernames;
    private Set<String> trainingNames;
}
