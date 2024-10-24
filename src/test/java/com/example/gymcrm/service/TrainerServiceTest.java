package com.example.gymcrm.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.gymcrm.dto.misc.UsernameAndPassword;
import com.example.gymcrm.dto.response.trainer.TrainerResponse;
import com.example.gymcrm.entity.Trainer;
import com.example.gymcrm.entity.TrainingType;
import com.example.gymcrm.entity.User;
import com.example.gymcrm.repository.TrainerRepository;
import com.example.gymcrm.repository.TrainingTypeRepository;
import com.example.gymcrm.repository.UserRepository;

class TrainerServiceTest {

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private TrainerService trainerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllTrainers() {
        List<Trainer> trainers = Arrays.asList(new Trainer(), new Trainer());
        when(trainerRepository.findAll()).thenReturn(trainers);

        List<Trainer> result = trainerService.getAllTrainers();

        assertEquals(trainers, result);
        verify(trainerRepository).findAll();
    }

    @Test
    void testGetTrainerById() {
        Trainer trainer = new Trainer();
        when(trainerRepository.findById(1)).thenReturn(Optional.of(trainer));

        Trainer result = trainerService.getTrainerById(1);

        assertEquals(trainer, result);
        verify(trainerRepository).findById(1);
    }

    @Test
    void testGetTrainerByUsername() {
        User user = new User();
        Trainer trainer = new Trainer();
        user.setTrainer(trainer);
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));

        Trainer result = trainerService.getTrainerByUsername("username");

        assertEquals(trainer, result);
        verify(userRepository).findByUsername("username");
    }

    @Test
    void testCreateTrainer() {
        String firstName = "John";
        String lastName = "Doe";
        String specialization = "Fitness";

        User user = new User();
        user.setUsername("John.Doe");
        user.setPassword("encodedPassword");

        Trainer trainer = new Trainer();
        trainer.setUser(user);

        TrainingType trainingType = new TrainingType();
        trainingType.setTypeName("Fitness");

        when(userRepository.save(any(User.class))).thenReturn(user);
        when(trainerRepository.save(any(Trainer.class))).thenReturn(trainer);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(trainingTypeRepository.findByTypeName("Fitness")).thenReturn(Optional.of(trainingType));

        UsernameAndPassword response = trainerService.createProfile(firstName, lastName, specialization);

        assertNotNull(response);
        assertEquals("John.Doe", response.getUsername());
        assertTrue(response.getPassword().matches("^[\\w\\d]{10}$"), "Пароль должен быть длиной 10 символов и содержать буквы и цифры");
        verify(userRepository).save(any(User.class));
        verify(trainerRepository).save(any(Trainer.class));
    }

    @Test
    void testUpdateTrainer() {
        String username = "username";
        String firstName = "Mary";
        String lastName = "Smith";
        String specialization = "Yoga";
        boolean isActive = true;

        User user = new User();
        Trainer trainer = new Trainer();
        trainer.setTrainees(new HashSet<>());
        user.setTrainer(trainer);
        trainer.setUser(user);

        TrainingType trainingType = new TrainingType();
        trainingType.setTypeName("Yoga");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(trainer);
        when(trainingTypeRepository.findByTypeName(specialization)).thenReturn(Optional.of(trainingType));

        TrainerResponse response = trainerService.updateTrainer(username, firstName, lastName, specialization, isActive);

        assertNotNull(response);
        assertEquals(firstName, trainer.getUser().getFirstName());
        assertEquals(lastName, trainer.getUser().getLastName());
        assertEquals(specialization, response.getSpecialization());
        verify(trainerRepository).save(trainer);
    }

    @Test
    void testGetTrainerProfile() {
        User user = new User();
        user.setFirstName("Alex");
        user.setLastName("Johnson");
        Trainer trainer = new Trainer();
        trainer.setUser(user);
        TrainingType trainingType = new TrainingType();
        trainingType.setTypeName("CrossFit");
        trainer.setSpecialization(trainingType);
        user.setTrainer(trainer);
        trainer.setTrainees(new HashSet<>());
        trainer.setTrainings(new HashSet<>());

        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));

        TrainerResponse response = trainerService.getTrainerForResponseByUsername("username");

        assertNotNull(response);
        assertEquals("Alex", response.getFirstName());
        assertEquals("Johnson", response.getLastName());
        assertEquals("CrossFit", response.getSpecialization());
    }
}

