package com.example.gymCRM.dao;

import com.example.gymCRM.entity.Training;
import com.example.gymCRM.entity.TrainingType;
import com.example.gymCRM.storage.TrainingStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class TrainingDAOTest {

    private TrainingDAO trainingDAO;
    private Map<String, Training> trainingMap;

    @BeforeEach
    void setUp() {
        TrainingStorage mockTrainingStorage = Mockito.mock(TrainingStorage.class);

        trainingMap = new HashMap<>();
        trainingMap.put("Strength1", new Training(1, 101, "Strength1", new TrainingType("STRENGTH"), LocalDate.of(2024, 9, 1), 60));
        trainingMap.put("Cardio1", new Training(2, 102, "Cardio1", new TrainingType("CARDIO"), LocalDate.of(2024, 9, 2), 45));


        when(mockTrainingStorage.getTrainings()).thenReturn(trainingMap);

        trainingDAO = new TrainingDAO(mockTrainingStorage);
    }

    @Test
    void testGetAll() {
        Map<String, Training> allTrainings = trainingDAO.getAll();
        assertEquals(2, allTrainings.size());
        assertTrue(allTrainings.containsKey("Strength1"));
        assertTrue(allTrainings.containsKey("Cardio1"));
    }

    @Test
    void testFindByTrainingName() {
        Training foundTraining = trainingDAO.findByTrainingName("Strength1");
        assertNotNull(foundTraining);
        assertEquals(1, foundTraining.getTraineeId());
        assertEquals(101, foundTraining.getTrainerId());
    }

    @Test
    void testAdd() {
        Training newTraining = new Training(3, 103, "Strength2", new TrainingType("STRENGTH"), LocalDate.of(2024, 9, 1), 60);
        trainingDAO.add(newTraining);

        assertEquals(3, trainingMap.size());
        assertTrue(trainingMap.containsKey("Strength2"));
        assertEquals(3, trainingMap.get("Strength2").getTraineeId());
    }

}