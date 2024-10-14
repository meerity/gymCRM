package com.example.gymcrm.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.net.URI;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import com.example.gymcrm.dto.TrainerDTO;
import com.example.gymcrm.dto.response.trainer.FourFieldsTrainerResponse;
import com.example.gymcrm.dto.response.trainer.TrainerResponse;
import com.example.gymcrm.dto.response.trainer.TrainerResponseWithUsername;
import com.example.gymcrm.dto.response.training.TrainingResponseForTrainer;
import com.example.gymcrm.dto.update.TrainerUpdateDTO;
import com.example.gymcrm.service.TrainerService;
import com.example.gymcrm.service.TrainingService;
import com.example.gymcrm.service.UserService;
import com.example.gymcrm.dto.misc.UsernameAndPassword;

class TrainerControllerTest {

    @Mock
    private TrainerService trainerService;
    @Mock
    private TrainingService trainingService;
    @Mock
    private UserService userService;
    @Mock
    private Errors errors;

    @InjectMocks
    private TrainerController trainerController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddTrainer_Success() {
        TrainerDTO trainerDTO = new TrainerDTO();
        trainerDTO.setFirstName("John");
        trainerDTO.setLastName("Doe");
        trainerDTO.setSpecialization("Cardio");

        when(errors.hasErrors()).thenReturn(false);
        when(trainerService.createProfile("John", "Doe", "Cardio")).thenReturn(new UsernameAndPassword("trainer1", "password1"));

        ResponseEntity<String> response = trainerController.addTrainer(trainerDTO, errors);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(URI.create("/api/trainers/trainer1"), response.getHeaders().getLocation());
        assertEquals("Username: trainer1\nPassword: password1", response.getBody());
        verify(trainerService).createProfile("John", "Doe", "Cardio");
    }

    @Test
    void testAddTrainer_ValidationErrors() {
        TrainerDTO trainerDTO = new TrainerDTO();
        when(errors.hasErrors()).thenReturn(true);

        ResponseEntity<String> response = trainerController.addTrainer(trainerDTO, errors);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        verifyNoInteractions(trainerService);
    }

    @Test
    void testGetTrainer() {
        String username = "trainer1";
        TrainerResponse mockResponse = new TrainerResponse();
        when(trainerService.getTrainerForResponseByUsername(username)).thenReturn(mockResponse);

        ResponseEntity<TrainerResponse> response = trainerController.getTrainer(username);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
    }

    @Test
    void testGetNotAssignedTrainers() {
        String traineeUsername = "trainee1";
        List<FourFieldsTrainerResponse> mockResponse = Arrays.asList(new FourFieldsTrainerResponse(), new FourFieldsTrainerResponse());
        when(trainerService.getNotAssignedActiveTrainersByTraineeUsername(traineeUsername)).thenReturn(mockResponse);

        ResponseEntity<List<FourFieldsTrainerResponse>> response = trainerController.getNotAssignedTrainers(traineeUsername);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
    }

    @Test
    void testGetTrainingsByTrainer() {
        String trainerUsername = "trainer1";
        LocalDate dateFrom = LocalDate.now().minusDays(7);
        LocalDate dateTo = LocalDate.now();
        String traineeFullname = "Jane Doe";
        String typeName = "Strength";

        List<TrainingResponseForTrainer> mockResponse = Arrays.asList(new TrainingResponseForTrainer(), new TrainingResponseForTrainer());
        when(userService.userIsTrainer(trainerUsername)).thenReturn(true);
        when(trainingService.getTrainingsWithCriteriaForTrainer(trainerUsername, dateFrom, dateTo, traineeFullname, typeName)).thenReturn(mockResponse);

        ResponseEntity<List<TrainingResponseForTrainer>> response = trainerController.getTrainingsByTrainer(trainerUsername, dateFrom, dateTo, traineeFullname, typeName);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
    }

    @Test
    void testUpdateTrainer_Success() {
        TrainerUpdateDTO trainerUpdateDTO = new TrainerUpdateDTO();
        trainerUpdateDTO.setFirstName("John");
        trainerUpdateDTO.setLastName("Doe");
        trainerUpdateDTO.setSpecialization("Cardio");
        trainerUpdateDTO.setActive(true);


        TrainerResponse mockResponse = new TrainerResponse();
        mockResponse.setFirstName("John");
        mockResponse.setLastName("Doe");
        mockResponse.setSpecialization("Cardio");
        mockResponse.setActive(true);

        TrainerResponseWithUsername mockResponseWithUsername = new TrainerResponseWithUsername("testTrainer", mockResponse);

        when(errors.hasErrors()).thenReturn(false);
        when(trainerService.updateTrainer("testTrainer", "John", "Doe", "Cardio", true)).thenReturn(mockResponse);

        ResponseEntity<Object> response = trainerController.updateTrainer("testTrainer", trainerUpdateDTO, errors);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponseWithUsername, response.getBody());
    }

    @Test
    void testUpdateTrainer_ValidationErrors() {
        TrainerUpdateDTO trainerUpdateDTO = new TrainerUpdateDTO();
        when(errors.hasErrors()).thenReturn(true);

        ResponseEntity<Object> response = trainerController.updateTrainer("testTrainer", trainerUpdateDTO, errors);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        verifyNoInteractions(trainerService);
    }

    @Test
    void testToggleActive() {
        ResponseEntity<String> response = trainerController.toggleActive("testTrainer");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService).toggleActive("testTrainer");
    }

    @Test
    void testGetTrainingsByTrainer_Unauthorized() {
        String trainerUsername = "trainer1";
        when(userService.userIsTrainer(trainerUsername)).thenReturn(false);

        ResponseEntity<List<TrainingResponseForTrainer>> response = trainerController.getTrainingsByTrainer(trainerUsername, null, null, null, null);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verifyNoInteractions(trainingService);
    }
}
