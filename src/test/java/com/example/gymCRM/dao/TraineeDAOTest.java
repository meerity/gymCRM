package com.example.gymCRM.dao;

import com.example.gymCRM.entity.Trainee;
import com.example.gymCRM.storage.TraineeStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TraineeDAOTest {

    private TraineeStorage mockTraineeStorage;
    private TraineeDAO traineeDAO;
    private Map<Integer, Trainee> traineeMap;

    @BeforeEach
    void setUp() {
        mockTraineeStorage = Mockito.mock(TraineeStorage.class);

        traineeMap = new HashMap<>();
        Trainee trainee1 = new Trainee("John", "Doe", "johndoe", "password123", true, LocalDate.of(1990, 1, 1), "123 Main St", 1);
        Trainee trainee2 = new Trainee("Jane", "Smith", "janesmith", "password456", true, LocalDate.of(1995, 5, 5), "456 Oak Ave", 2);
        traineeMap.put(1, trainee1);
        traineeMap.put(2, trainee2);

        when(mockTraineeStorage.getTrainees()).thenReturn(traineeMap);

        traineeDAO = new TraineeDAO(mockTraineeStorage);
    }

    @Test
    void testGetAll() {
        Map<Integer, Trainee> allTrainees = traineeDAO.getAll();
        assertEquals(2, allTrainees.size());
        assertTrue(allTrainees.containsKey(1));
        assertTrue(allTrainees.containsKey(2));
    }

    @Test
    void testFindById() {
        Trainee foundTrainee = traineeDAO.findById(1);
        assertNotNull(foundTrainee);
        assertEquals("John", foundTrainee.getFirstName());
        assertEquals("Doe", foundTrainee.getLastName());
    }

    @Test
    void testFindByFirstNameAndLastName() {
        List<Trainee> foundTrainees = traineeDAO.findByFirstNameAndLastName("Jane", "Smith");
        assertEquals(1, foundTrainees.size());
        assertEquals("Jane", foundTrainees.getFirst().getFirstName());
        assertEquals("Smith", foundTrainees.getFirst().getLastName());
    }

    @Test
    void testAdd() {
        Trainee newTrainee = new Trainee("Michael", "Johnson", "mikejohn", "password789", true, LocalDate.of(2000, 10, 10), "789 Pine St", 3);
        traineeDAO.add(newTrainee);

        assertEquals(3, traineeMap.size());
        assertTrue(traineeMap.containsKey(3));
        assertEquals("Michael", traineeMap.get(3).getFirstName());
    }

    @Test
    void testUpdate() {
        Trainee updatedTrainee = new Trainee("John", "Doe", "johndoe", "newpassword", true, LocalDate.of(1990, 1, 1), "123 Main St", 1);
        traineeDAO.update(updatedTrainee);

        assertEquals("newpassword", traineeMap.get(1).getPassword());
    }

    @Test
    void testDeleteTrainee() {
        boolean result = traineeDAO.deleteTrainee(1);

        assertTrue(result);
        assertEquals(1, traineeMap.size());
        assertFalse(traineeMap.containsKey(1));
    }

    @Test
    void testDeleteNonExistentTrainee() {
        boolean result = traineeDAO.deleteTrainee(99);

        assertFalse(result);
    }
}
