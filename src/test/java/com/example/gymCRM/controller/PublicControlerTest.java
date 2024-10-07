package com.example.gymcrm.controller;

import com.example.gymcrm.dto.TraineeDTO;
import com.example.gymcrm.dto.TrainerDTO;
import com.example.gymcrm.service.TraineeService;
import com.example.gymcrm.service.TrainerService;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PublicControlerTest {

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private PublicControler publicControler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddTrainer_Success() {
        TrainerDTO trainerDTO = new TrainerDTO();
        when(bindingResult.hasErrors()).thenReturn(false);
        when(trainerService.createProfile(trainerDTO)).thenReturn(Pair.of("trainer1", "password1"));

        ResponseEntity<String> response = publicControler.addTrainer(trainerDTO, bindingResult);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Username: trainer1\nPassword: password1", response.getBody());
        verify(trainerService).createProfile(trainerDTO);
    }

    @Test
    void testAddTrainer_ValidationErrors() {
        TrainerDTO trainerDTO = new TrainerDTO();
        when(bindingResult.hasErrors()).thenReturn(true);

        ResponseEntity<String> response = publicControler.addTrainer(trainerDTO, bindingResult);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verifyNoInteractions(trainerService);
    }

    @Test
    void testAddTrainee_Success() {
        TraineeDTO traineeDTO = new TraineeDTO();
        when(bindingResult.hasErrors()).thenReturn(false);
        when(traineeService.createProfile(traineeDTO)).thenReturn(Pair.of("trainee1", "password1"));

        ResponseEntity<String> response = publicControler.addTrainee(traineeDTO, bindingResult);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Username: trainee1\nPassword: password1", response.getBody());
        verify(traineeService).createProfile(traineeDTO);
    }

    @Test
    void testAddTrainee_ValidationErrors() {
        TraineeDTO traineeDTO = new TraineeDTO();
        when(bindingResult.hasErrors()).thenReturn(true);

        ResponseEntity<String> response = publicControler.addTrainee(traineeDTO, bindingResult);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verifyNoInteractions(traineeService);
    }
}

