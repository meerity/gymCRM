package com.example.gymcrm.dto.response.training;

import com.example.gymcrm.dto.response.base.BaseTrainingResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TrainingResponseForTrainer extends BaseTrainingResponse {
    private String traineeFullname;
}
