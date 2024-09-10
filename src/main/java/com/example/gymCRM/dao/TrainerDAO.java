package com.example.gymCRM.dao;

import com.example.gymCRM.entity.Trainer;
import com.example.gymCRM.storage.TrainerStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class TrainerDAO {

    private final TrainerStorage trainerStorage;
    private final Map<Integer, Trainer> trainers;

    public TrainerDAO(TrainerStorage trainerStorage) {
        this.trainerStorage = trainerStorage;
        trainers = trainerStorage.getTrainers();
    }

    public Map<Integer, Trainer> getAll() {
        return trainerStorage.getTrainers();
    }

    public Trainer findById(int id) {
        return trainers.get(id);
    }

    public List<Trainer> findByFirstNameAndLastName(String firstName, String lastName) {
        List<Trainer> trainerList = trainers.values().stream()
                .filter(t -> t.getFirstName().equals(firstName))
                .filter(t -> t.getLastName().equals(lastName))
                .toList();
        if (trainerList.isEmpty()) {
            log.warn("No trainer found for firstName {} and lastName {}", firstName, lastName);
        } else {
            log.info("Trainers found for firstName {} and lastName {}", firstName, lastName);
        }
        return trainerList;
    }

    public void add(Trainer trainer) {
        if (trainers.containsKey(trainer.getUserId())) {
            log.warn("Trainer with this ID already exists.");
        } else {
            trainers.put(trainer.getUserId(), trainer);
            log.warn("Trainer added successfully: {} {}", trainer.getFirstName(), trainer.getLastName());
        }
    }


    public void update(Trainer trainer) {
        if (trainers.get(trainer.getUserId()) != null) {
            trainers.replace(trainer.getUserId(), trainer);
        } else {
            log.error("trainer with {} not found", trainer.getUserId());
        }
    }
}
