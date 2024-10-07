package com.example.gymcrm.controller;

import com.example.gymcrm.dto.TrainingDTO;
import com.example.gymcrm.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.validation.Errors;
import java.util.Collection;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AddControllerTest {

    @Mock
    private TrainingService trainingService;

    @Mock
    private Authentication authentication;

    @Mock
    private Errors errors;

    @InjectMocks
    private AddController addController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddTraining_AsTrainer_Success() {
        Collection<? extends GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_TRAINER"));
        when(authentication.getAuthorities()).thenAnswer(invocation -> authorities);
        when(errors.hasErrors()).thenReturn(false);

        TrainingDTO trainingDTO = new TrainingDTO();
        ResponseEntity<String> response = addController.postMethodName(trainingDTO, errors, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Successfully created new Training!", response.getBody());
        verify(trainingService).addTraining(trainingDTO);
    }

    @Test
    void testAddTraining_AsNonTrainer_Forbidden() {
        Collection<? extends GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_TRAINEE"));
        when(authentication.getAuthorities()).thenAnswer(invocation -> authorities);
        when(errors.hasErrors()).thenReturn(false);

        TrainingDTO trainingDTO = new TrainingDTO();
        ResponseEntity<String> response = addController.postMethodName(trainingDTO, errors, authentication);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(trainingService, never()).addTraining(any());
    }

    @Test
    void testAddTraining_ValidationErrors() {
        when(errors.hasErrors()).thenReturn(true);

        TrainingDTO trainingDTO = new TrainingDTO();
        ResponseEntity<String> response = addController.postMethodName(trainingDTO, errors, authentication);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(trainingService, never()).addTraining(any());
    }
}

