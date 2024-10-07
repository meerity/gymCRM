package com.example.gymcrm.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Data
public class TrainingDTO {
    @NotBlank(message = "Trainer username cannot be blank")
    private String trainerUsername;

    @NotBlank(message = "Trainee username cannot be blank")
    private String traineeUsername;

    @NotBlank(message = "Training name cannot be blank")
    private String trainingName;

    @NotBlank(message = "Training type name cannot be blank")
    private String trainingTypeName;

    @Positive(message = "Duration must be a positive number")
    private int duration;
}
