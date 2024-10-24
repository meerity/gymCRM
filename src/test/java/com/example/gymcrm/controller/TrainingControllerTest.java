package com.example.gymcrm.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.example.gymcrm.dto.TrainingDTO;

import static org.mockito.ArgumentMatchers.anyInt;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.Errors;
import com.example.gymcrm.service.TrainingService;

class TrainingControllerTest {

    @Mock
    private TrainingService trainingService;

    @Mock
    private Errors errors;

    @InjectMocks
    private TrainingController trainingController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddTraining_Success() {
        TrainingDTO trainingDTO = new TrainingDTO();
        trainingDTO.setTraineeUsername("trainee1");
        trainingDTO.setTrainerUsername("trainer1");
        trainingDTO.setTrainingName("Strength Training");
        trainingDTO.setTrainingDate(LocalDate.now());
        trainingDTO.setDuration(60);

        when(errors.hasErrors()).thenReturn(false);

        ResponseEntity<String> response = trainingController.addTraining(trainingDTO, errors);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Successfully created new Training", response.getBody());
        verify(trainingService).addTraining(
            trainingDTO.getTrainerUsername(),
            trainingDTO.getTraineeUsername(),
            trainingDTO.getTrainingName(),
            trainingDTO.getTrainingDate(),
            trainingDTO.getDuration()
        );
    }

    @Test
    void testAddTraining_ValidationErrors() {
        TrainingDTO trainingDTO = new TrainingDTO();
        when(errors.hasErrors()).thenReturn(true);

        ResponseEntity<String> response = trainingController.addTraining(trainingDTO, errors);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        verify(trainingService, never()).addTraining(any(), any(), any(), any(), anyInt());
    }
}
