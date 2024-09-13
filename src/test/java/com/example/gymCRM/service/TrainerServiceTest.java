package com.example.gymCRM.service;

import com.example.gymCRM.dao.TrainerDAO;
import com.example.gymCRM.entity.Trainer;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

public class TrainerServiceTest {

    @Mock
    private TrainerDAO trainerDAO;

    @InjectMocks
    private TrainerService trainerService;

    @Test
    public void shouldUpdateTrainerSuccessfully() {
        Trainer existingTrainer = new Trainer("Jane", "Doe", "Jane.Doe1", "12345", true, "Yoga", 1);
        when(trainerDAO.findById(1)).thenReturn(existingTrainer);

        boolean result = trainerService.updateTrainer(1, "Jane", "Smith", "Pilates", true);

        // Then
        verify(trainerDAO, times(1)).update(argThat(trainer ->
                "Jane".equals(trainer.getFirstName()) &&
                        "Smith".equals(trainer.getLastName()) &&
                        "Pilates".equals(trainer.getSpecialization()) &&
                        trainer.isActive()));

        assertTrue(result);
    }

    @Test
    public void shouldNotCreateTrainerWhenFieldsAreMissing() {
        trainerService.addTrainer("", "", "", true);

        verify(trainerDAO, never()).add(any(Trainer.class));
    }

    @Test
    public void shouldHandleNullTrainerForUpdate() {
        when(trainerDAO.findById(1)).thenReturn(null);

        boolean result = trainerService.updateTrainer(1, "Jane", "Smith", "Pilates", true);

        verify(trainerDAO, never()).update(any(Trainer.class));
        assertFalse(result);
    }
}
