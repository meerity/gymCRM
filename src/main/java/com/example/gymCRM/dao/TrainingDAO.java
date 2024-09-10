package com.example.gymCRM.dao;

import com.example.gymCRM.entity.Training;
import com.example.gymCRM.storage.TrainingStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;


import java.util.Map;

@Slf4j
@Repository
public class TrainingDAO {

    private final TrainingStorage trainingStorage;
    private final Map<String, Training> trainings;

    public TrainingDAO(TrainingStorage trainingStorage) {
        this.trainingStorage = trainingStorage;
        trainings = trainingStorage.getTrainings();
    }

    public Map<String, Training> getAll() {
        return trainingStorage.getTrainings();
    }

    public Training findByTrainingName(String name) {
        return trainings.get(name);
    }

    public void add(Training training) {
        if (trainings.containsKey(training.getTrainingName())) {
            log.warn("Training with this name already exists.");
        } else {
            trainings.put(training.getTrainingName(), training);
            log.info("Training added successfully: {}", training.getTrainingName());
        }
    }
}
