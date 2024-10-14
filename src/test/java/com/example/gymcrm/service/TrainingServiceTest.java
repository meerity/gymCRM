package com.example.gymcrm.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.example.gymcrm.dto.response.training.TrainingResponseForTrainee;
import com.example.gymcrm.dto.response.training.TrainingResponseForTrainer;
import com.example.gymcrm.entity.Trainee;
import com.example.gymcrm.entity.Trainer;
import com.example.gymcrm.entity.Training;
import com.example.gymcrm.entity.TrainingType;
import com.example.gymcrm.entity.User;
import com.example.gymcrm.repository.TraineeRepository;
import com.example.gymcrm.repository.TrainerRepository;
import com.example.gymcrm.repository.TrainingRepository;
import com.example.gymcrm.repository.TrainingTypeRepository;
import com.example.gymcrm.repository.UserRepository;

class TrainingServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TraineeRepository traineeRepository;

    @InjectMocks
    private TrainingService trainingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetTrainingsWithCriteria_ForTrainer() {
        User user = new User();
        user.setUsername("John.Doe");
        Trainer trainer = new Trainer();
        user.setTrainer(trainer);
        trainer.setUser(user);

        Training training = new Training();
        training.setTrainingName("Test Training");
        training.setTrainingDate(LocalDate.now());
        training.setDuration(60);
        training.setTrainer(trainer);

        TrainingType trainingType = new TrainingType();
        trainingType.setTypeName("Fitness");
        training.setTrainingType(trainingType);

        Trainee trainee = new Trainee();
        User traineeUser = new User();
        traineeUser.setUsername("Jane.Smith");
        trainee.setUser(traineeUser);
        training.setTrainee(trainee);

        trainer.setTrainings(new HashSet<>(Arrays.asList(training)));

        when(userRepository.findByUsername("John.Doe")).thenReturn(Optional.of(user));

        List<TrainingResponseForTrainer> result = trainingService.getTrainingsWithCriteriaForTrainer("John.Doe", null, null, null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Training", result.get(0).getTrainingName());
        assertEquals("Fitness", result.get(0).getTypeName());
    }

    @Test
    void testGetTrainingsWithCriteria_ForTrainee() {
        User user = new User();
        user.setUsername("Jane.Smith");
        Trainee trainee = new Trainee();
        user.setTrainee(trainee);
        trainee.setUser(user);

        Training training = new Training();
        training.setTrainingName("Test Training");
        training.setTrainingDate(LocalDate.now());
        training.setDuration(60);
        training.setTrainee(trainee);

        TrainingType trainingType = new TrainingType();
        trainingType.setTypeName("Yoga");
        training.setTrainingType(trainingType);

        Trainer trainer = new Trainer();
        User trainerUser = new User();
        trainerUser.setUsername("John.Doe");
        trainer.setUser(trainerUser);
        training.setTrainer(trainer);

        trainee.setTrainings(new HashSet<>(Arrays.asList(training)));

        when(userRepository.findByUsername("Jane.Smith")).thenReturn(Optional.of(user));

        List<TrainingResponseForTrainee> result = trainingService.getTrainingsWithCriteriaForTrainee("Jane.Smith", null, null, null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Training", result.get(0).getTrainingName());
        assertEquals("Yoga", result.get(0).getTypeName());
    }

    @Test
    void testGetTrainingsWithCriteria_WithFilters() {
        User user = new User();
        user.setUsername("Jane.Smith");
        Trainee trainee = new Trainee();
        user.setTrainee(trainee);
        trainee.setUser(user);

        
        TrainingType yogaType = new TrainingType();
        yogaType.setTypeName("Yoga");
        
        TrainingType fitnessType = new TrainingType();
        fitnessType.setTypeName("Fitness");
        
        Trainer trainer1 = new Trainer();
        User trainerUser1 = new User();
        trainerUser1.setUsername("John.Doe");
        trainerUser1.setFirstName("John");
        trainerUser1.setLastName("Doe");
        trainer1.setUser(trainerUser1);
        
        Trainer trainer2 = new Trainer();
        User trainerUser2 = new User();
        trainerUser2.setUsername("John.Smith");
        trainerUser2.setFirstName("John");
        trainerUser2.setLastName("Smith");
        trainer2.setUser(trainerUser2);

        trainee.setTrainers(new HashSet<>(Arrays.asList(trainer1, trainer2)));
        
        Training training1 = new Training();
        training1.setTrainingName("Yoga Session");
        training1.setTrainingDate(LocalDate.of(2023, 5, 1));
        training1.setDuration(60);
        training1.setTrainingType(yogaType);
        training1.setTrainee(trainee);
        training1.setTrainer(trainer1);
        
        Training training2 = new Training();
        training2.setTrainingName("Fitness Class");
        training2.setTrainingDate(LocalDate.of(2023, 5, 15));
        training2.setDuration(45);
        training2.setTrainingType(fitnessType);
        training2.setTrainee(trainee);
        training2.setTrainer(trainer2);
        
        trainee.setTrainings(new HashSet<>(Arrays.asList(training1, training2)));

        when(userRepository.findByUsername("Jane.Smith")).thenReturn(Optional.of(user));

        List<TrainingResponseForTrainee> result = trainingService.getTrainingsWithCriteriaForTrainee(
            "Jane.Smith", 
            LocalDate.of(2023, 5, 1), 
            LocalDate.of(2023, 5, 31), 
            "John Doe", 
            "Yoga"
        );

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Yoga Session", result.get(0).getTrainingName());
        assertEquals("Yoga", result.get(0).getTypeName());
    }

    @Test
    void testGetTrainingsWithCriteria_UserNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> 
            trainingService.getTrainingsWithCriteriaForTrainee("nonexistent", null, null, null, null)
        );
    }
}

