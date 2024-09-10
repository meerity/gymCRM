package com.example.gymCRM.dao;

import com.example.gymCRM.entity.Trainee;
import com.example.gymCRM.storage.TraineeStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class TraineeDAO {

    private final TraineeStorage traineeStorage;
    private final Map<Integer, Trainee> trainees;

    public TraineeDAO(TraineeStorage traineeStorage) {
        this.traineeStorage = traineeStorage;
        trainees = traineeStorage.getTrainees();
    }

    public Map<Integer, Trainee> getAll() {
        return traineeStorage.getTrainees();
    }

    public Trainee findById(int id) {
        return trainees.get(id);
    }

    public List<Trainee> findByFirstNameAndLastName(String firstName, String lastName) {
        List<Trainee> traineeList = trainees.values().stream()
                .filter(t -> t.getFirstName().equals(firstName))
                .filter(t -> t.getLastName().equals(lastName))
                .toList();
        if (traineeList.isEmpty()) {
            log.warn("No trainee found for firstName {} and lastName {}", firstName, lastName);
        } else {
            log.info("Trainees found for firstName {} and lastName {}", firstName, lastName);
        }
        return traineeList;
    }

    public void add(Trainee trainee) {
        if (trainees.containsKey(trainee.getUserId())) {
            log.warn("Trainee with this ID already exists.");
        } else {
            trainees.put(trainee.getUserId(), trainee);
            log.warn("Trainee added successfully: {} {}", trainee.getFirstName(), trainee.getLastName());
        }
    }


    public void update(Trainee trainee) {
        if (trainees.get(trainee.getUserId()) != null) {
            trainees.replace(trainee.getUserId(), trainee);
        } else {
            log.error("trainee with {} not found, failed to update", trainee.getUserId());
        }
    }

    public boolean deleteTrainee(int id) {
        if (trainees.containsKey(id)) {
            trainees.remove(id);
            return true;
        } else {
            log.error("trainee with {} not found, failed to delete", id);
            return false;
        }
    }
}
