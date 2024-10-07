package com.example.gymcrm.controller;

import com.example.gymcrm.entity.Trainee;
import com.example.gymcrm.service.TraineeService;
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
import java.util.Collection;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class DeleteControllerTest {

    @Mock
    private TraineeService traineeService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private DeleteController deleteController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testDeleteTrainee_AsTrainer_Success() {
        Collection<? extends GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_TRAINER"));
        when(authentication.getAuthorities()).thenAnswer(invocation -> authorities);

        ResponseEntity<Trainee> response = deleteController.deleteTrainee("testUser", authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(traineeService).deleteTrainee("testUser");
    }

    @Test
    void testDeleteTrainee_AsNonTrainer_Forbidden() {
        Collection<? extends GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_TRAINEE"));
        when(authentication.getAuthorities()).thenAnswer(invocation -> authorities);

        ResponseEntity<Trainee> response = deleteController.deleteTrainee("testUser", authentication);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(traineeService, never()).deleteTrainee(anyString());
    }
}
