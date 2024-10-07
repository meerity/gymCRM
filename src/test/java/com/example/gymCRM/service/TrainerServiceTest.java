package com.example.gymcrm.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.gymcrm.dto.TrainerDTO;
import com.example.gymcrm.dto.response.TrainerResponse;
import com.example.gymcrm.dto.update.TrainerUpdateDTO;
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
        TrainerDTO trainerDTO = new TrainerDTO();
        trainerDTO.setFirstName("John");
        trainerDTO.setLastName("Doe");
        trainerDTO.setSpecialization("Fitness");
        trainerDTO.setIsActive(true);

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

        Pair<String, String> response = trainerService.createProfile(trainerDTO);

        assertNotNull(response);
        assertEquals("John.Doe", response.getLeft());
        assertTrue(response.getRight().matches("^[\\w\\d]{10}$"), "Password must be 10 characters long and contain letters and numbers");
        verify(userRepository).save(any(User.class));
        verify(trainerRepository).save(any(Trainer.class));
    }

    @Test
    void testUpdateTrainer() {
        TrainerUpdateDTO updateDTO = new TrainerUpdateDTO();
        updateDTO.setFirstName("Mary");
        updateDTO.setLastName("Smith");
        updateDTO.setSpecialization("Yoga");

        User user = new User();
        Trainer trainer = new Trainer();
        user.setTrainer(trainer);
        trainer.setUser(user);

        TrainingType trainingType = new TrainingType();
        trainingType.setTypeName("Yoga");

        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(trainer);
        when(trainingTypeRepository.findByTypeName("Yoga")).thenReturn(Optional.of(trainingType));

        String response = trainerService.updateTrainer("username", updateDTO);

        assertNotNull(response);
        assertEquals("Mary", trainer.getUser().getFirstName());
        assertEquals("Smith", trainer.getUser().getLastName());
        assertEquals("Mary.Smith", response);
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

