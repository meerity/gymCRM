package com.example.gymCRM.service;

import com.example.gymCRM.dao.TrainerDAO;
import com.example.gymCRM.entity.Trainer;
import com.example.gymCRM.security.PasswordGenerator;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TrainerService {

    private final TrainerDAO trainerDAO;
    private PasswordGenerator passwordGenerator;
    private int currentIndex;

    public TrainerService(TrainerDAO trainerDAO) {
        this.trainerDAO = trainerDAO;
    }

    public List<Trainer> getAllTrainers() {
        return trainerDAO.getAll().values().stream().toList();
    }

    public Trainer getTrainerById(int id) {
        return trainerDAO.findById(id);
    }

    public void addTrainer(String firstName, String lastName, String specialization, boolean isActive) {
        if (!firstName.trim().isEmpty() && !lastName.trim().isEmpty() && !specialization.trim().isEmpty()) {
            Trainer newTrainer = new Trainer();
            newTrainer.setFirstName(firstName);
            newTrainer.setLastName(lastName);
            createUserName(newTrainer, firstName, lastName);
            newTrainer.setPassword(passwordGenerator.generatePassword());
            newTrainer.setActive(isActive);
            newTrainer.setSpecialization(specialization);
            newTrainer.setUserId(++currentIndex);
            trainerDAO.add(newTrainer);
            log.info("New trainer added: {} {}", newTrainer.getFirstName(), newTrainer.getLastName());
        } else {
            log.error("One of fields is missing. Please try again.");
        }
    }

    public boolean updateTrainer(int id, String firstName, String lastName, String specialization, boolean isActive) {
        Trainer trainer = trainerDAO.findById(id);
        if (trainer == null) {
            log.error("Trainer with id {} not found", id);
            return false;
        }
        if (firstName.isEmpty()) {
            firstName = trainer.getFirstName();
            log.info("firstname is empty, skipping editing");
        }
        if (lastName.isEmpty()) {
            lastName = trainer.getLastName();
            log.info("lastname is empty, skipping editing");
        }
        if (trainer.getFirstName().equals(firstName) && trainer.getLastName().equals(lastName)) {
            log.info("Fullname is equal, skipping editing fullname and username");
        } else {
            createUserName(trainer, firstName, lastName);
            trainer.setFirstName(firstName);
            trainer.setLastName(lastName);
        }
        if (!specialization.isEmpty()) {
            if (trainer.getSpecialization().equals(specialization)) {
                log.info("Specialization is equal, skipping editing specialization");
            } else {
                trainer.setSpecialization(specialization);
            }
        } else {
            log.info("Specialization is empty, skipping editing.");
        }
        trainer.setActive(isActive);

        trainerDAO.update(trainer);
        log.info("Trainer updated: {} {}", trainer.getFirstName(), trainer.getLastName());
        return true;
    }

    @Autowired
    protected void setPasswordGenerator(PasswordGenerator passwordGenerator) {
        this.passwordGenerator = passwordGenerator;
    }

    @PostConstruct
    private void init() {
        currentIndex = trainerDAO.getAll().size();
    }

    private void createUserName(Trainer trainer, String firstName, String lastName) {
        List<Trainer> trainersWithSameFullName = trainerDAO.findByFirstNameAndLastName(firstName, lastName);
        if (trainersWithSameFullName.isEmpty()) {
            trainer.setUsername(firstName + "." + lastName);
        } else {
            trainer.setUsername(firstName + "." + lastName + trainersWithSameFullName.size());
        }
    }

}
