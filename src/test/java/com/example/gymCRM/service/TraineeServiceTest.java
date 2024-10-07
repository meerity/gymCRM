package com.example.gymcrm.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.gymcrm.dto.TraineeDTO;
import com.example.gymcrm.dto.response.TraineeResponse;
import com.example.gymcrm.dto.update.TraineeUpdateDTO;
import com.example.gymcrm.entity.Trainee;
import com.example.gymcrm.entity.Trainer;
import com.example.gymcrm.entity.User;
import com.example.gymcrm.repository.TraineeRepository;
import com.example.gymcrm.repository.UserRepository;

class TraineeServiceTest {

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private TraineeService traineeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllTrainees() {
        List<Trainee> trainees = Arrays.asList(new Trainee(), new Trainee());
        when(traineeRepository.findAll()).thenReturn(trainees);

        List<Trainee> result = traineeService.getAllTrainees();

        assertEquals(trainees, result);
        verify(traineeRepository).findAll();
    }

    @Test
    void testGetTraineeById() {
        Trainee trainee = new Trainee();
        when(traineeRepository.findById(1)).thenReturn(Optional.of(trainee));

        Trainee result = traineeService.getTraineeById(1);

        assertEquals(trainee, result);
        verify(traineeRepository).findById(1);
    }

    @Test
    void testGetTraineeByUsername() {
        User user = new User();
        Trainee trainee = new Trainee();
        user.setTrainee(trainee);
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));

        Trainee result = traineeService.getTraineeByUsername("username");

        assertEquals(trainee, result);
        verify(userRepository).findByUsername("username");
    }

    @Test
    void testGetTraineeByUsernameThrowsException() {
        User user = new User();
        user.setTrainer(new Trainer());
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));

        assertThrows(NoSuchElementException.class, () -> traineeService.getTraineeByUsername("username"));
    }

    @Test
    void testCreateTrainee() {
        TraineeDTO traineeDTO = new TraineeDTO();
        traineeDTO.setFirstName("John");
        traineeDTO.setLastName("Doe");
        traineeDTO.setAddress("123 Main St");
        traineeDTO.setDateOfBirth(LocalDate.of(1990, 1, 1));
        traineeDTO.setIsActive(true);

        User user = new User();
        user.setUsername("John.Doe");
        user.setPassword("encodedPassword");

        Trainee trainee = new Trainee();
        trainee.setUser(user);

        when(userRepository.save(any(User.class))).thenReturn(user);
        when(traineeRepository.save(any(Trainee.class))).thenReturn(trainee);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        Pair<String, String> response = traineeService.createProfile(traineeDTO);

        assertNotNull(response);
        assertEquals("John.Doe", response.getLeft());
        assertTrue(response.getRight().matches("^[\\w\\d]{10}$"), "Password must be 10 characters long and contain letters and numbers");
        verify(userRepository).save(any(User.class));
        verify(traineeRepository).save(any(Trainee.class));
    }

    @Test
    void testUpdateTrainee() {
        TraineeUpdateDTO updateDTO = new TraineeUpdateDTO();
        updateDTO.setFirstName("Jane");
        updateDTO.setLastName("Smith");
        updateDTO.setAddress("456 Oak Ave");
        updateDTO.setDateOfBirth(LocalDate.of(1995, 5, 5));

        User user = new User();
        Trainee trainee = new Trainee();
        user.setTrainee(trainee);
        trainee.setUser(user);

        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        when(traineeRepository.save(any(Trainee.class))).thenReturn(trainee);

        String response = traineeService.updateTrainee("username", updateDTO);

        assertNotNull(response);
        assertEquals("Jane", trainee.getUser().getFirstName());
        assertEquals("Smith", trainee.getUser().getLastName());
        assertEquals("456 Oak Ave", trainee.getAddress());
        assertEquals("Jane.Smith", response);
        verify(traineeRepository).save(trainee);
    }

    @Test
    void testDeleteTrainee() {
        User user = new User();
        Trainee trainee = new Trainee();
        user.setTrainee(trainee);

        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));

        traineeService.deleteTrainee("username");

        verify(traineeRepository).delete(trainee);
        verify(userRepository).delete(user);
    }

    @Test
    void testGetTraineeProfile() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        Trainee trainee = new Trainee();
        trainee.setUser(user);
        trainee.setAddress("123 Main St");
        user.setTrainee(trainee);
        trainee.setTrainers(new HashSet<>());
        trainee.setTrainings(new HashSet<>());
        

        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));

        TraineeResponse response = traineeService.getTraineeForResponseByUsername("username");

        assertNotNull(response);
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        assertEquals("123 Main St", response.getAddress());
    }

    @Test
    void testUpdateTraineeTrainers() {
        User traineeUser = new User();
        traineeUser.setUsername("Jane.Smith");
        Trainee trainee = new Trainee();
        traineeUser.setTrainee(trainee);
        trainee.setUser(traineeUser);
        trainee.setTrainers(new HashSet<>());

        User trainer1User = new User();
        trainer1User.setUsername("John.Doe");
        Trainer trainer1 = new Trainer();
        trainer1User.setTrainer(trainer1);
        trainer1.setUser(trainer1User);
        trainer1.setTrainees(new HashSet<>());


        User trainer2User = new User();
        trainer2User.setUsername("John.Doe1");
        Trainer trainer2 = new Trainer();
        trainer2User.setTrainer(trainer2);
        trainer2.setUser(trainer2User);
        trainer2.setTrainees(new HashSet<>());

        List<String> trainerUsernames = Arrays.asList("John.Doe", "John.Doe1");

        when(userRepository.findByUsername("Jane.Smith")).thenReturn(Optional.of(traineeUser));
        when(userRepository.findAllByUsernameIn(trainerUsernames)).thenReturn(Arrays.asList(trainer1User, trainer2User));

        traineeService.updateTraineeTrainers("Jane.Smith", trainerUsernames);

        verify(traineeRepository).save(trainee);
        assertEquals(2, trainee.getTrainers().size());
        assertTrue(trainee.getTrainers().contains(trainer1));
        assertTrue(trainee.getTrainers().contains(trainer2));
    }  
}

