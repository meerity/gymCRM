package com.example.gymcrm.controller;

import com.example.gymcrm.dto.update.PasswordChangeDTO;
import com.example.gymcrm.dto.update.TraineeUpdateDTO;
import com.example.gymcrm.dto.update.TrainerUpdateDTO;
import com.example.gymcrm.service.TraineeService;
import com.example.gymcrm.service.TrainerService;
import com.example.gymcrm.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.Errors;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UpdateControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private Authentication authentication;

    @Mock
    private Errors errors;

    @InjectMocks
    private UpdateController updateController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdatePassword_Success() {
        PasswordChangeDTO passwordDTO = new PasswordChangeDTO();
        when(authentication.getName()).thenReturn("testUser");
        when(errors.hasErrors()).thenReturn(false);

        ResponseEntity<String> response = updateController.updatePassword(passwordDTO, errors, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService).changePassword("testUser", passwordDTO);
    }

    @Test
    void testUpdatePassword_ValidationErrors() {
        PasswordChangeDTO passwordDTO = new PasswordChangeDTO();
        when(errors.hasErrors()).thenReturn(true);

        ResponseEntity<String> response = updateController.updatePassword(passwordDTO, errors, authentication);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(userService, never()).changePassword(anyString(), any());
    }

    @Test
    void testUpdateTrainer_Success() {
        TrainerUpdateDTO trainerUpdateDTO = new TrainerUpdateDTO();
        trainerUpdateDTO.setFirstName("John");
        when(authentication.getName()).thenReturn("testTrainer");
        when(errors.hasErrors()).thenReturn(false);
        when(trainerService.updateTrainer("testTrainer", trainerUpdateDTO)).thenReturn("newUsername");

        ResponseEntity<String> response = updateController.updateTrainer(trainerUpdateDTO, errors, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Username after update: newUsername", response.getBody());
    }

    @Test
    void testUpdateTrainee_Success() {
        TraineeUpdateDTO traineeUpdateDTO = new TraineeUpdateDTO();
        traineeUpdateDTO.setLastName("Doe");
        when(authentication.getName()).thenReturn("testTrainee");
        when(errors.hasErrors()).thenReturn(false);
        when(traineeService.updateTrainee("testTrainee", traineeUpdateDTO)).thenReturn("newUsername");

        ResponseEntity<String> response = updateController.updateTrainee(traineeUpdateDTO, errors, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Username after update: newUsername", response.getBody());
    }

    @Test
    void testToggleActive() {
        when(authentication.getName()).thenReturn("testUser");

        ResponseEntity<String> response = updateController.updatePassword(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService).toggleActive("testUser");
    }

    @Test
    void testUpdateTraineeTrainers() {
        List<String> trainerUsernames = new ArrayList<>();
        trainerUsernames.add("trainer1");
        trainerUsernames.add("trainer2");
        when(authentication.getName()).thenReturn("testTrainee");

        ResponseEntity<String> response = updateController.updateTraineeTrainers(trainerUsernames, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Trainers list updated successfully", response.getBody());
        verify(traineeService).updateTraineeTrainers("testTrainee", trainerUsernames);
    }
}

