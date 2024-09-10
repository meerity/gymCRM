package com.example.gymCRM.service;

import com.example.gymCRM.dao.TrainingDAO;
import com.example.gymCRM.entity.Training;
import com.example.gymCRM.entity.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TrainingServiceTest {

    @Mock
    private TrainingDAO trainingDAO;

    @InjectMocks
    private TrainingService trainingService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldReturnAllTrainings() {
        Map<String, Training> trainingsMap = new HashMap<>();
        Training training1 = new Training(1, 2, "Yoga", new TrainingType("Strength"), LocalDate.now(), 60);
        Training training2 = new Training(2, 3, "Pilates", new TrainingType("Strength"), LocalDate.now(), 45);
        trainingsMap.put("Yoga", training1);
        trainingsMap.put("Pilates", training2);

        when(trainingDAO.getAll()).thenReturn(trainingsMap);

        List<Training> result = trainingService.getAllTrainings();

        assertEquals(2, result.size());
        assertTrue(result.contains(training1));
        assertTrue(result.contains(training2));
    }

    @Test
    public void shouldReturnTrainingByName() {
        Training expectedTraining = new Training(1, 2, "Yoga", new TrainingType("Strength"), LocalDate.now(), 60);
        when(trainingDAO.findByTrainingName("Yoga")).thenReturn(expectedTraining);

        Training result = trainingService.getTrainingByName("Yoga");

        assertNotNull(result);
        assertEquals(expectedTraining, result);
    }

    @Test
    public void shouldAddTrainingSuccessfully() {
        int trainerId = 1;
        int traineeId = 2;
        String trainingName = "Yoga";
        TrainingType trainingType = new TrainingType("Strength");
        LocalDate trainingDate = LocalDate.of(2024, 9, 10);
        int trainingDuration = 60;

        trainingService.addTraining(trainerId, traineeId, trainingName, trainingType, trainingDate, trainingDuration);

        ArgumentCaptor<Training> trainingCaptor = ArgumentCaptor.forClass(Training.class);
        verify(trainingDAO, times(1)).add(trainingCaptor.capture());

        Training capturedTraining = trainingCaptor.getValue();
        assertEquals(trainerId, capturedTraining.getTrainerId());
        assertEquals(traineeId, capturedTraining.getTraineeId());
        assertEquals(trainingName, capturedTraining.getTrainingName());
        assertEquals(trainingType, capturedTraining.getTrainingType());
        assertEquals(trainingDate, capturedTraining.getTrainingDate());
        assertEquals(trainingDuration, capturedTraining.getTrainingDuration());
    }

    @Test
    public void shouldNotAddTrainingWhenFieldsAreMissing() {
        trainingService.addTraining(1, 2, "", null, null, 60);

        verify(trainingDAO, never()).add(any(Training.class));
    }

    @Test
    public void shouldHandleNullTrainingWhenGettingByName() {
        when(trainingDAO.findByTrainingName("NonExistentTraining")).thenReturn(null);

        Training result = trainingService.getTrainingByName("NonExistentTraining");

        assertNull(result);
    }
}
