package com.example.gymcrm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import com.example.gymcrm.dto.TrainingDTO;
import com.example.gymcrm.dto.response.TrainingResponse;
import com.example.gymcrm.entity.Training;
import com.example.gymcrm.entity.TrainingType;
import com.example.gymcrm.entity.User;
import com.example.gymcrm.repository.TraineeRepository;
import com.example.gymcrm.repository.TrainerRepository;
import com.example.gymcrm.repository.TrainingRepository;
import com.example.gymcrm.repository.TrainingTypeRepository;
import com.example.gymcrm.repository.UserRepository;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingService {

    private final UserRepository userRepository;
    private final TrainingRepository trainingRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;
    
    
    public List<TrainingResponse> getTrainingsWithCriteria(String username, LocalDate dateFrom, LocalDate dateTo, String fullName, String typeName) throws NoSuchElementException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("Cannot find User by this username: " + username));
        List<Training> trainings;

        if (user.getTrainer() != null) {
            List<Training> unfilteredTrainings = user.getTrainer().getTrainings().stream().toList();
            trainings = filterTrainings(unfilteredTrainings, dateFrom, dateTo, fullName, false, typeName);
        } else {
            List<Training> unfilteredTrainings = user.getTrainee().getTrainings().stream().toList();
            trainings = filterTrainings(unfilteredTrainings, dateFrom, dateTo, fullName, true, typeName);
        }
        return trainings.stream()
                .map(training -> {
                    TrainingResponse response = new TrainingResponse();
                    response.setTraineeUsername(training.getTrainee().getUser().getUsername());
                    response.setTrainerUsername(training.getTrainer().getUser().getUsername());
                    response.setTrainingName(training.getTrainingName());
                    response.setTypeName(training.getTrainingType().getTypeName());
                    response.setTrainingDate(training.getTrainingDate());
                    response.setDuration(training.getDuration());
                    return response;
                })
                .toList();
    }

    private List<Training> filterTrainings(List<Training> trainings, LocalDate dateFrom, LocalDate dateTo, String fullName, boolean searchForTrainer, String typeName) {
    return trainings.stream()
        .filter(training -> (dateFrom == null || training.getTrainingDate().isAfter(dateFrom) || training.getTrainingDate().isEqual(dateFrom)))
        .filter(training -> (dateTo == null || training.getTrainingDate().isBefore(dateTo) || training.getTrainingDate().isEqual(dateTo)))
        .filter(training -> (fullName == null || fullName.isEmpty() ||
            (searchForTrainer
                ? (training.getTrainer().getUser().getFirstName() + " " + training.getTrainer().getUser().getLastName())
                : (training.getTrainee().getUser().getFirstName() + " " + training.getTrainee().getUser().getLastName()))
                .equalsIgnoreCase(fullName)))
        .filter(training -> (typeName == null || typeName.isEmpty() ||
            training.getTrainingType().getTypeName().equalsIgnoreCase(typeName)))
        .toList();
}

    public boolean addTraining(TrainingDTO trainingDTO) throws NoSuchElementException {
        User userTrainer = userRepository.findByUsername(trainingDTO.getTrainerUsername())
                .orElseThrow(() -> new NoSuchElementException("Cannot find Trainer by this username: " + trainingDTO.getTrainerUsername()));

        User userTrainee = userRepository.findByUsername(trainingDTO.getTraineeUsername())
                .orElseThrow(() -> new NoSuchElementException("Cannot find Trainee by this username: " + trainingDTO.getTraineeUsername()));

        if (userTrainee.getTrainee() == null) {
            throw new NoSuchElementException("Invalid trainee");
        }
        
        if (userTrainer.getTrainer() == null) {
            throw new NoSuchElementException("Invalid trainer");
        }
        
        Training training = new Training();
        training.setTrainer(userTrainer.getTrainer());
        training.setTrainee(userTrainee.getTrainee());
        training.setTrainingName(trainingDTO.getTrainingName());

        TrainingType type = trainingTypeRepository.findByTypeName(trainingDTO.getTrainingTypeName())
        .orElseThrow(() -> new NoSuchElementException("Cannot find TrainingType by this name: " + trainingDTO.getTrainingTypeName()));

        training.setTrainingType(type);
        training.setTrainingDate(LocalDate.now());
        training.setDuration(trainingDTO.getDuration());

        userTrainer.getTrainer().getTrainings().add(training);
        userTrainee.getTrainee().getTrainings().add(training);

        trainingRepository.save(training);
        trainerRepository.save(userTrainer.getTrainer());
        traineeRepository.save(userTrainee.getTrainee());
        return true;
    }


}
