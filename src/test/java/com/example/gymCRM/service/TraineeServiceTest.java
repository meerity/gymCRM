package com.example.gymCRM.service;

import com.example.gymCRM.dao.TraineeDAO;
import com.example.gymCRM.entity.Trainee;
import com.example.gymCRM.security.PasswordGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TraineeServiceTest {

    @Mock
    private TraineeDAO traineeDAO;

    @Mock
    private PasswordGenerator passwordGenerator;

    @InjectMocks
    private TraineeService traineeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        traineeService.setPasswordGenerator(passwordGenerator);
    }

    @Test
    void shouldAddTraineeWhenAllFieldsAreValid() {
        // given
        String firstName = "John";
        String lastName = "Doe";
        LocalDate dateOfBirth = LocalDate.of(1990, 1, 1);
        String address = "123 Main St";
        boolean isActive = true;

        when(passwordGenerator.generatePassword()).thenReturn("securePassword123");

        // when
        traineeService.addTrainee(firstName, lastName, dateOfBirth, address, isActive);

        // then
        ArgumentCaptor<Trainee> captor = ArgumentCaptor.forClass(Trainee.class);
        verify(traineeDAO, times(1)).add(captor.capture());
        Trainee savedTrainee = captor.getValue();

        assertEquals("John", savedTrainee.getFirstName());
        assertEquals("Doe", savedTrainee.getLastName());
        assertEquals("securePassword123", savedTrainee.getPassword());
        assertTrue(savedTrainee.isActive());
        assertNotNull(savedTrainee.getUsername());
        assertEquals("123 Main St", savedTrainee.getAddress());
    }

    @Test
    void shouldNotAddTraineeWhenFirstNameIsBlank() {
        // given
        String firstName = "";
        String lastName = "Doe";
        LocalDate dateOfBirth = LocalDate.of(1990, 1, 1);
        String address = "123 Main St";
        boolean isActive = true;

        // when
        traineeService.addTrainee(firstName, lastName, dateOfBirth, address, isActive);

        // then
        verify(traineeDAO, never()).add(any(Trainee.class));
    }

    @Test
    void shouldUpdateTraineeSuccessfullyWhenDataIsChanged() {
        // given
        Trainee existingTrainee = new Trainee("Jane", "Doe", "janedoe", "oldPassword", true, LocalDate.of(1990, 1, 1), "456 Another St", 1);
        when(traineeDAO.findById(1)).thenReturn(existingTrainee);

        // when
        boolean result = traineeService.updateTrainee(1, "Jane", "Smith", LocalDate.of(1990, 1, 1), "789 New St", false);

        // then
        assertTrue(result);
        verify(traineeDAO, times(1)).update(existingTrainee);
        assertEquals("789 New St", existingTrainee.getAddress());
        assertFalse(existingTrainee.isActive());
        assertEquals("Jane.Smith", existingTrainee.getUsername());
    }

    @Test
    void shouldNotUpdateTraineeIfNotFound() {
        // given
        when(traineeDAO.findById(999)).thenReturn(null);

        // when
        boolean result = traineeService.updateTrainee(999, "John", "Smith", LocalDate.of(1990, 1, 1), "New St", true);

        // then
        assertFalse(result);
        verify(traineeDAO, never()).update(any(Trainee.class));
    }

    @Test
    void shouldDeleteTraineeSuccessfully() {
        // given
        when(traineeDAO.deleteTrainee(1)).thenReturn(true);

        // when
        boolean result = traineeService.deleteTrainee(1);

        // then
        assertTrue(result);
        verify(traineeDAO, times(1)).deleteTrainee(1);
    }

    @Test
    void shouldNotDeleteTraineeIfNotFound() {
        // given
        when(traineeDAO.deleteTrainee(1)).thenReturn(false);

        // when
        boolean result = traineeService.deleteTrainee(1);

        // then
        assertFalse(result);
        verify(traineeDAO, times(1)).deleteTrainee(1);
    }
}
