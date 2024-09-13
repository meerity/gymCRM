package com.example.gymCRM.service;

import com.example.gymCRM.dao.TraineeDAO;
import com.example.gymCRM.entity.Trainee;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class TraineeService {

    private final TraineeDAO traineeDAO;
    private int currentIndex;

    public TraineeService(TraineeDAO traineeDAO) {
        this.traineeDAO = traineeDAO;
    }

    public List<Trainee> getAllTrainees() {
        return traineeDAO.getAll().values().stream().toList();
    }

    public Trainee getTraineeById(int id) {
        return traineeDAO.findById(id);
    }

    public void addTrainee(String firstName, String lastName, LocalDate dateOfBirth, String address, boolean isActive) {
        if (!firstName.isBlank() && !lastName.isBlank() && dateOfBirth != null && !address.isBlank()) {
            Trainee newTrainee = new Trainee();
            newTrainee.setFirstName(firstName);
            newTrainee.setLastName(lastName);
            createUserName(newTrainee, firstName, lastName);
            String password = RandomStringUtils.secure().nextAlphanumeric(10);
            newTrainee.setPassword(password);
            newTrainee.setActive(isActive);
            newTrainee.setDateOfBirth(dateOfBirth);
            newTrainee.setAddress(address);
            newTrainee.setUserId(++currentIndex);
            traineeDAO.add(newTrainee);
            log.info("New trainee added: {} {}", newTrainee.getFirstName(), newTrainee.getLastName());
        } else {
            log.error("One of fields is missing. Please try again.");
        }
    }

    public boolean updateTrainee(int id, String firstName, String lastName, LocalDate dateOfBirth, String address, boolean isActive) {
        Trainee trainee = traineeDAO.findById(id);

        if (trainee == null) {
            log.error("Trainee with id {} not found", id);
            return false;
        }

        if (firstName.isEmpty()) {
            firstName = trainee.getFirstName();
            log.info("firstname is empty, skipping editing");
        }
        if (lastName.isEmpty()) {
            lastName = trainee.getLastName();
            log.info("lastname is empty, skipping editing");
        }
        if (trainee.getFirstName().equals(firstName) && trainee.getLastName().equals(lastName)) {
            log.info("Fullname is equal, skipping editing fullname and username");
        } else {
            createUserName(trainee, firstName, lastName);
            trainee.setFirstName(firstName);
            trainee.setLastName(lastName);
        }

        if (dateOfBirth != null) {
            if (trainee.getDateOfBirth().equals(dateOfBirth)) {
                log.info("Date of birth is equal, skipping editing");
            } else {
                trainee.setDateOfBirth(dateOfBirth);
            }
        } else {
            log.info("Date of birth is empty, skipping editing.");
        }

        if (!address.isEmpty()) {
            if (trainee.getAddress().equals(address)) {
                log.info("Address is equal, skipping editing");
            } else {
                trainee.setAddress(address);
            }
        }
        trainee.setActive(isActive);

        traineeDAO.update(trainee);
        log.info("Trainee updated: {} {}", trainee.getFirstName(), trainee.getLastName());
        return true;
    }

    public boolean deleteTrainee(int id) {
        if (traineeDAO.deleteTrainee(id)) {
            log.info("Trainee with id {} deleted", id);
            return true;
        } else {
            log.error("Failed to delete trainee with id {}", id);
            return false;
        }
    }

    @PostConstruct
    private void init() {
        currentIndex = traineeDAO.getAll().size();
    }

    private void createUserName(Trainee trainee, String firstName, String lastName) {
        List<Trainee> traineesWithSameFullName = traineeDAO.findByFirstNameAndLastName(firstName, lastName);
        if (traineesWithSameFullName.isEmpty()) {
            trainee.setUsername(firstName + "." + lastName);
        } else {
            trainee.setUsername(firstName + "." + lastName + traineesWithSameFullName.size());
        }
    }
}
