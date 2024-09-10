package com.example.gymCRM.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class Training {
    private int traineeId;
    private int trainerId;
    private String trainingName;
    private TrainingType trainingType;
    private LocalDate trainingDate;
    private int trainingDuration;

    @JsonCreator
    public Training(@JsonProperty("traineeId") int traineeId,
                    @JsonProperty("trainerId") int trainerId,
                    @JsonProperty("trainingName") String trainingName,
                    @JsonProperty("trainingType") TrainingType trainingType,
                    @JsonProperty("trainingDate") LocalDate trainingDate,
                    @JsonProperty("trainingDuration") int trainingDuration) {
        this.traineeId = traineeId;
        this.trainerId = trainerId;
        this.trainingName = trainingName;
        this.trainingType = trainingType;
        this.trainingDate = trainingDate;
        this.trainingDuration = trainingDuration;
    }

    @Override
    public String toString() {
        return "Trainer Id: " + trainerId + ", Trainee Id: " + traineeId + ", Training Name: " + trainingName + ", Training Type: " + trainingType + ", Training Date: " + trainingDate + ", Training Duration: " + trainingDuration;
    }
}
