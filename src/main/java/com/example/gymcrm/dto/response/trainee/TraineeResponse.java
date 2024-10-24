package com.example.gymcrm.dto.response.trainee;

import java.time.LocalDate;
import java.util.List;
import lombok.Data;

@Data
public class TraineeResponse {

    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String address;
    private boolean active;
    private List<TrainerInfo> trainers;

    @Data
    public class TrainerInfo {
        private String username;
        private String firstName;
        private String lastName;
        private String specialization;
    }
}
