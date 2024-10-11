package com.example.gymcrm.dto;

import lombok.Data;
import java.time.LocalDate;
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

    @NotBlank(message = "Training date cannot be null")
    private LocalDate trainingDate;

    @Positive(message = "Duration must be a positive number")
    private int duration;
}
