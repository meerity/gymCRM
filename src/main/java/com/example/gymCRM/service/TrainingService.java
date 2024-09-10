package com.example.gymCRM.service;

import com.example.gymCRM.dao.TrainingDAO;
import com.example.gymCRM.entity.Training;
import com.example.gymCRM.entity.TrainingType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class TrainingService {

    private final TrainingDAO trainingDAO;

    public TrainingService(TrainingDAO trainingDAO) {
        this.trainingDAO = trainingDAO;
    }

    public List<Training> getAllTrainings() {
        return trainingDAO.getAll().values().stream().toList();
    }

    public Training getTrainingByName(String name) {
        return trainingDAO.findByTrainingName(name);
    }

    public void addTraining(int trainerId, int traineeId, String trainingName, TrainingType trainingType, LocalDate trainingDate, int trainingDuration) {
        if (!trainingName.trim().isEmpty() && trainingType != null && trainingDate != null) {
            Training newTraining = new Training();
            newTraining.setTrainerId(trainerId);
            newTraining.setTraineeId(traineeId);
            newTraining.setTrainingName(trainingName);
            newTraining.setTrainingType(trainingType);
            newTraining.setTrainingDate(trainingDate);
            newTraining.setTrainingDuration(trainingDuration);
            trainingDAO.add(newTraining);
            log.info("New training added: {}", newTraining.getTrainingName());
        } else {
            log.error("One of fields is missing. Please try again.");
        }
    }

}
