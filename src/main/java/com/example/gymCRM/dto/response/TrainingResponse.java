package com.example.gymcrm.dto.response;

import java.time.LocalDate;
import lombok.Data;

@Data
public class TrainingResponse {
    private String traineeUsername;
    private String trainerUsername;
    private String trainingName;
    private String typeName;
    private LocalDate trainingDate;
    private int duration;
}
