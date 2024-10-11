package com.example.gymcrm.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import java.time.LocalDate;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import com.example.gymcrm.dto.TraineeDTO;
import com.example.gymcrm.dto.response.trainee.TraineeResponse;
import com.example.gymcrm.dto.response.trainee.TraineeResponseWithUsername;
import com.example.gymcrm.dto.response.training.TrainingResponseForTrainee;
import com.example.gymcrm.dto.update.TraineeUpdateDTO;
import com.example.gymcrm.service.TraineeService;
import com.example.gymcrm.service.TrainerService;
import com.example.gymcrm.service.TrainingService;
import com.example.gymcrm.service.UserService;
import com.example.gymcrm.dto.misc.UsernameAndPassword;

class TraineeControllerTest {

    @Mock
    private Errors errors;
    @Mock
    private TraineeService traineeService;
    @Mock
    private TrainingService trainingService;
    @Mock
    private TrainerService trainerService;
    @Mock
    private UserService userService;

    @InjectMocks
    private TraineeController traineeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddTrainee_Success() {
        TraineeDTO traineeDTO = new TraineeDTO();
        traineeDTO.setFirstName("John");
        traineeDTO.setLastName("Doe");
        traineeDTO.setAddress("123 Main St");
        traineeDTO.setDateOfBirth(LocalDate.of(1990, 1, 1));

        when(errors.hasErrors()).thenReturn(false);
        when(traineeService.createProfile(anyString(), anyString(), any(), any()))
            .thenReturn(new UsernameAndPassword("trainee1", "password1"));

        ResponseEntity<String> response = traineeController.addTrainee(traineeDTO, errors);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        String responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.contains("trainee1"));
        assertTrue(responseBody.contains("password1"));
        verify(traineeService).createProfile(anyString(), anyString(), any(), any());
    }

    @Test
    void testAddTrainee_ValidationErrors() {
        TraineeDTO traineeDTO = new TraineeDTO();
        when(errors.hasErrors()).thenReturn(true);

        ResponseEntity<String> response = traineeController.addTrainee(traineeDTO, errors);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verifyNoInteractions(traineeService);
    }

    @Test
    void testGetTrainee() {
        String username = "trainee1";
        TraineeResponse mockResponse = new TraineeResponse();
        when(traineeService.getTraineeForResponseByUsername(username)).thenReturn(mockResponse);

        ResponseEntity<TraineeResponse> response = traineeController.getTrainee(username);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
    }

    @Test
    void testGetTrainingsByTrainee() {
        String traineeUsername = "trainee1";
        LocalDate dateFrom = LocalDate.now().minusDays(7);
        LocalDate dateTo = LocalDate.now();
        String trainerFullname = "John Doe";
        String typeName = "Cardio";

        List<TrainingResponseForTrainee> mockResponse = Arrays.asList(new TrainingResponseForTrainee(), new TrainingResponseForTrainee());
        when(userService.userIsTrainer(traineeUsername)).thenReturn(false);
        when(trainingService.getTrainingsWithCriteriaForTrainee(traineeUsername, dateFrom, dateTo, trainerFullname, typeName)).thenReturn(mockResponse);

        ResponseEntity<List<TrainingResponseForTrainee>> response = traineeController.getTrainingsByTrainee(traineeUsername, dateFrom, dateTo, trainerFullname, typeName);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
    }

    

    @Test
    void testUpdateTrainee_Success() {
        TraineeUpdateDTO traineeUpdateDTO = new TraineeUpdateDTO();
        traineeUpdateDTO.setFirstName("John");
        traineeUpdateDTO.setLastName("Doe");
        traineeUpdateDTO.setDateOfBirth(LocalDate.of(1990, 1, 1));
        traineeUpdateDTO.setAddress("123 Main St");
        traineeUpdateDTO.setIsActive(true);

        TraineeResponse mockResponse = new TraineeResponse();
        mockResponse.setFirstName("John");
        mockResponse.setLastName("Doe");
        mockResponse.setDateOfBirth(LocalDate.of(1990, 1, 1));
        mockResponse.setAddress("123 Main St");
        mockResponse.setActive(true);
        TraineeResponseWithUsername mockResponseWithUsername = new TraineeResponseWithUsername("testTrainee", mockResponse);

        when(errors.hasErrors()).thenReturn(false);
        when(traineeService.updateTrainee(eq("testTrainee"), anyString(), anyString(), any(), anyString(), anyBoolean())).thenReturn(mockResponse);

        ResponseEntity<Object> response = traineeController.updateTrainee("testTrainee", traineeUpdateDTO, errors);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponseWithUsername, response.getBody());
    }

    @Test
    void testToggleActive() {
        ResponseEntity<String> response = traineeController.toggleActive("testUser");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService).toggleActive("testUser");
    }

    @Test
    void testDeleteTrainee_Success() {
        ResponseEntity<String> response = traineeController.deleteTrainee("testUser");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(traineeService).deleteTrainee("testUser");
    }

    @Test
    void testGetTrainingsByTrainee_AsTrainer_Forbidden() {
        String traineeUsername = "trainee1";
        when(userService.userIsTrainer(traineeUsername)).thenReturn(true);

        ResponseEntity<List<TrainingResponseForTrainee>> response = traineeController.getTrainingsByTrainee(traineeUsername, null, null, null, null);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verifyNoInteractions(trainingService);
    }

    @Test
    void testUpdateTrainee_ValidationErrors() {
        TraineeUpdateDTO traineeUpdateDTO = new TraineeUpdateDTO();
        when(errors.hasErrors()).thenReturn(true);

        ResponseEntity<Object> response = traineeController.updateTrainee("testTrainee", traineeUpdateDTO, errors);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        verifyNoInteractions(traineeService);
    }
}
