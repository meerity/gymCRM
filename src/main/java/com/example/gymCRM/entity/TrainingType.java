package com.example.gymCRM.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainingType {
    private String trainingTypeName;

    @Override
    public String toString() {
        return trainingTypeName;
    }
}
