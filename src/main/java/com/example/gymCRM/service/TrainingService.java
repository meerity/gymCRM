package com.example.gymcrm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import com.example.gymcrm.dto.response.training.TrainingResponseForTrainee;
import com.example.gymcrm.dto.response.training.TrainingResponseForTrainer;
import com.example.gymcrm.entity.Training;
import com.example.gymcrm.entity.TrainingType;
import com.example.gymcrm.entity.User;
import com.example.gymcrm.repository.TraineeRepository;
import com.example.gymcrm.repository.TrainerRepository;
import com.example.gymcrm.repository.TrainingRepository;
import com.example.gymcrm.repository.UserRepository;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingService {

    private final UserRepository userRepository;
    private final TrainingRepository trainingRepository;
    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;

    public List<TrainingResponseForTrainer> getTrainingsWithCriteriaForTrainer(String username, LocalDate dateFrom, LocalDate dateTo, String fullName, String typeName) throws NoSuchElementException {
        boolean isTrainer = true;

;
        List<Training> trainings = prepareTrainings(username, isTrainer);
        trainings = filterTrainings(trainings, dateFrom, dateTo, fullName, isTrainer, typeName);

        return trainings.stream()
                .map(training -> {
                    TrainingResponseForTrainer response = new TrainingResponseForTrainer();
                    response.setTrainingName(training.getTrainingName());
                    response.setTrainingDate(training.getTrainingDate());
                    response.setTypeName(training.getTrainingType().getTypeName());
                    response.setDuration(training.getDuration());
                    response.setTraineeFullname(training.getTrainee().getUser().getFirstName() + " " + training.getTrainee().getUser().getLastName());
                    return response;
                })
                .toList();
    }
    
    public List<TrainingResponseForTrainee> getTrainingsWithCriteriaForTrainee(String username, LocalDate dateFrom, LocalDate dateTo, String fullName, String typeName) throws NoSuchElementException {
        boolean isTrainer = false;

        List<Training> trainings = prepareTrainings(username, isTrainer);
        trainings = filterTrainings(trainings, dateFrom, dateTo, fullName, isTrainer, typeName);

        return trainings.stream()
                .map(training -> {
                    TrainingResponseForTrainee response = new TrainingResponseForTrainee();
                    response.setTrainingName(training.getTrainingName());
                    response.setTrainingDate(training.getTrainingDate());
                    response.setTypeName(training.getTrainingType().getTypeName());
                    response.setDuration(training.getDuration());
                    response.setTrainerFullname(training.getTrainer().getUser().getFirstName() + " " + training.getTrainer().getUser().getLastName());
                    return response;
                })
                .toList();
    }

    private List<Training> prepareTrainings(String username, boolean isTrainer) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("Cannot find User by this username: " + username));
        List<Training> trainings;

        if (isTrainer) {
            trainings = user.getTrainer().getTrainings().stream().toList();
        } else {
            trainings = user.getTrainee().getTrainings().stream().toList();
        }
        return trainings;
    }

    private List<Training> filterTrainings(List<Training> trainings, LocalDate dateFrom, LocalDate dateTo, String fullName, boolean searchForTraineeFullName, String typeName) {
    return trainings.stream()
        .filter(training -> (dateFrom == null || training.getTrainingDate().isAfter(dateFrom) || training.getTrainingDate().isEqual(dateFrom)))
        .filter(training -> (dateTo == null || training.getTrainingDate().isBefore(dateTo) || training.getTrainingDate().isEqual(dateTo)))
        .filter(training -> (fullName == null || fullName.isEmpty() ||
            (searchForTraineeFullName
                ? (training.getTrainee().getUser().getFirstName() + " " + training.getTrainee().getUser().getLastName())
                : (training.getTrainer().getUser().getFirstName() + " " + training.getTrainer().getUser().getLastName()))
                .equalsIgnoreCase(fullName)))
                .peek(training -> System.out.println(training.getTrainingDate()))
        .filter(training -> (typeName == null || typeName.isEmpty() ||
            training.getTrainingType().getTypeName().equalsIgnoreCase(typeName)))
            .toList();
    }

    public boolean addTraining(String trainerUsername, String traineeUsername, String trainingName, LocalDate trainingDate, int duration) throws NoSuchElementException {
        User userTrainer = userRepository.findByUsername(trainerUsername)
                .orElseThrow(() -> new NoSuchElementException("Cannot find Trainer by this username: " + trainerUsername));

        User userTrainee = userRepository.findByUsername(traineeUsername)
                .orElseThrow(() -> new NoSuchElementException("Cannot find Trainee by this username: " + traineeUsername));

        if (userTrainee.getTrainee() == null) {
            throw new NoSuchElementException("Invalid trainee");
        }
        
        if (userTrainer.getTrainer() == null) {
            throw new NoSuchElementException("Invalid trainer");
        }
        
        Training training = new Training();
        training.setTrainer(userTrainer.getTrainer());
        training.setTrainee(userTrainee.getTrainee());
        training.setTrainingName(trainingName);

        TrainingType type = userTrainer.getTrainer().getSpecialization();
        training.setTrainingType(type);
        training.setTrainingDate(trainingDate);
        training.setDuration(duration);

        userTrainer.getTrainer().getTrainings().add(training);
        userTrainee.getTrainee().getTrainings().add(training);

        trainingRepository.save(training);
        trainerRepository.save(userTrainer.getTrainer());
        traineeRepository.save(userTrainee.getTrainee());
        return true;
    }


}
