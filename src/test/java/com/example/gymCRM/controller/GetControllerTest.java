package com.example.gymcrm.controller;

import com.example.gymcrm.dto.response.TraineeResponse;
import com.example.gymcrm.dto.response.TrainerResponse;
import com.example.gymcrm.dto.response.TrainingResponse;
import com.example.gymcrm.service.TraineeService;
import com.example.gymcrm.service.TrainerService;
import com.example.gymcrm.service.TrainingService;
import com.example.gymcrm.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class GetControllerTest {

    @Mock
    private TrainerService trainerService;

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainingService trainingService;

    @Mock
    private UserService userService;

    @InjectMocks
    private GetController getController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetTrainer() {
        String username = "trainer1";
        TrainerResponse mockResponse = new TrainerResponse();
        when(trainerService.getTrainerForResponseByUsername(username)).thenReturn(mockResponse);

        ResponseEntity<TrainerResponse> response = getController.getTrainer(username);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
    }

    @Test
    void testGetTrainee() {
        String username = "trainee1";
        TraineeResponse mockResponse = new TraineeResponse();
        when(traineeService.getTraineeForResponseByUsername(username)).thenReturn(mockResponse);

        ResponseEntity<TraineeResponse> response = getController.getTrainee(username);

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

        List<TrainingResponse> mockResponse = Arrays.asList(new TrainingResponse(), new TrainingResponse());
        when(userService.userIsTrainer(traineeUsername)).thenReturn(false);
        when(trainingService.getTrainingsWithCriteria(traineeUsername, dateFrom, dateTo, trainerFullname, typeName)).thenReturn(mockResponse);

        ResponseEntity<List<TrainingResponse>> response = getController.getTrainingsByTrainee(traineeUsername, dateFrom, dateTo, trainerFullname, typeName);

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

        List<TrainingResponse> mockResponse = Arrays.asList(new TrainingResponse(), new TrainingResponse());
        when(userService.userIsTrainer(trainerUsername)).thenReturn(true);
        when(trainingService.getTrainingsWithCriteria(trainerUsername, dateFrom, dateTo, traineeFullname, typeName)).thenReturn(mockResponse);

        ResponseEntity<List<TrainingResponse>> response = getController.getTrainingsByTrainer(trainerUsername, dateFrom, dateTo, traineeFullname, typeName);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
    }

    @Test
    void testGetFreeTrainers() {
        String traineeUsername = "trainee1";
        List<TrainerResponse> mockResponse = Arrays.asList(new TrainerResponse(), new TrainerResponse());
        when(trainerService.getNotAssignedTrainersByTraineeUsername(traineeUsername)).thenReturn(mockResponse);

        ResponseEntity<List<TrainerResponse>> response = getController.getMethodName(traineeUsername);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
    }
}

