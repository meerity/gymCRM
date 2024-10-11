package com.example.gymcrm.dto.response.base;

import java.time.LocalDate;
import lombok.Data;

@Data
public class BaseTrainingResponse {
    private String trainingName;
    private LocalDate trainingDate;
    private String typeName;
    private int duration;
}
