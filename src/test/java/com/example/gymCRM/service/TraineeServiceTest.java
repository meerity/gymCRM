package com.example.gymCRM.service;

import com.example.gymCRM.dao.TraineeDAO;
import com.example.gymCRM.entity.Trainee;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {

    @Mock
    private TraineeDAO traineeDAO;

    @InjectMocks
    private TraineeService traineeService;


    @Test
    void shouldAddTraineeWhenAllFieldsAreValid() {
        String firstName = "John";
        String lastName = "Doe";
        LocalDate dateOfBirth = LocalDate.of(1990, 1, 1);
        String address = "123 Main St";
        boolean isActive = true;

        traineeService.addTrainee(firstName, lastName, dateOfBirth, address, isActive);

        ArgumentCaptor<Trainee> captor = ArgumentCaptor.forClass(Trainee.class);
        verify(traineeDAO, times(1)).add(captor.capture());
        Trainee savedTrainee = captor.getValue();

        assertEquals("John", savedTrainee.getFirstName());
        assertEquals("Doe", savedTrainee.getLastName());
        assertTrue(savedTrainee.isActive());
        assertEquals("John.Doe", savedTrainee.getUsername());
        assertEquals("123 Main St", savedTrainee.getAddress());
    }

    @Test
    void shouldNotAddTraineeWhenFirstNameIsBlank() {
        String firstName = "";
        String lastName = "Doe";
        LocalDate dateOfBirth = LocalDate.of(1990, 1, 1);
        String address = "123 Main St";
        boolean isActive = true;

        traineeService.addTrainee(firstName, lastName, dateOfBirth, address, isActive);

        verify(traineeDAO, never()).add(any(Trainee.class));
    }

    @Test
    void shouldUpdateTraineeSuccessfullyWhenDataIsChanged() {
        Trainee existingTrainee = new Trainee("Jane", "Doe", "Jane.Doe", "oldPassword", true, LocalDate.of(1990, 1, 1), "456 Another St", 1);
        when(traineeDAO.findById(1)).thenReturn(existingTrainee);

        boolean result = traineeService.updateTrainee(1, "Jane", "Smith", LocalDate.of(1990, 1, 1), "789 New St", false);

        assertTrue(result);
        verify(traineeDAO, times(1)).update(existingTrainee);
        assertEquals("789 New St", existingTrainee.getAddress());
        assertFalse(existingTrainee.isActive());
        assertEquals("Jane.Smith", existingTrainee.getUsername());
    }

    @Test
    void shouldNotUpdateTraineeIfNotFound() {
        when(traineeDAO.findById(999)).thenReturn(null);

        boolean result = traineeService.updateTrainee(999, "John", "Smith", LocalDate.of(1990, 1, 1), "New St", true);

        assertFalse(result);
        verify(traineeDAO, never()).update(any(Trainee.class));
    }

    @Test
    void shouldDeleteTraineeSuccessfully() {
        when(traineeDAO.deleteTrainee(1)).thenReturn(true);

        boolean result = traineeService.deleteTrainee(1);

        assertTrue(result);
        verify(traineeDAO, times(1)).deleteTrainee(1);
    }

    @Test
    void shouldNotDeleteTraineeIfNotFound() {
        when(traineeDAO.deleteTrainee(1)).thenReturn(false);

        boolean result = traineeService.deleteTrainee(1);

        assertFalse(result);
        verify(traineeDAO, times(1)).deleteTrainee(1);
    }
}
