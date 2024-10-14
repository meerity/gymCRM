package com.example.gymcrm.dto.response.training;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.example.gymcrm.dto.response.base.BaseTrainingResponse;

@Data
@EqualsAndHashCode(callSuper = true)
public class TrainingResponseForTrainee extends BaseTrainingResponse {
    private String trainerFullname;
}
