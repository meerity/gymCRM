package com.example.gymCRM.dao;

import com.example.gymCRM.entity.Trainer;
import com.example.gymCRM.storage.TrainerStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class TrainerDAOTest {

    private TrainerDAO trainerDAO;
    private Map<Integer, Trainer> trainerMap;

    @BeforeEach
    void setUp() {
        TrainerStorage mockTrainerStorage = Mockito.mock(TrainerStorage.class);

        trainerMap = new HashMap<>();
        trainerMap.put(1, new Trainer("John", "Doe", "johndoe", "password123", true, "PE", 1));
        trainerMap.put(2, new Trainer("Jane", "Smith", "janesmith", "password456", true,"PE", 2));

        when(mockTrainerStorage.getTrainers()).thenReturn(trainerMap);

        trainerDAO = new TrainerDAO(mockTrainerStorage);

    }

    @Test
    void testGetAll() {
        Map<Integer, Trainer> allTrainers = trainerDAO.getAll();
        assertEquals(2, allTrainers.size());
        assertTrue(allTrainers.containsKey(1));
        assertTrue(allTrainers.containsKey(2));
    }

    @Test
    void testFindById() {
        Trainer foundTrainer = trainerDAO.findById(1);
        assertNotNull(foundTrainer);
        assertEquals("John", foundTrainer.getFirstName());
        assertEquals("Doe", foundTrainer.getLastName());
    }

    @Test
    void testFindByFirstNameAndLastName() {
        List<Trainer> foundTrainers = trainerDAO.findByFirstNameAndLastName("Jane", "Smith");
        assertEquals(1, foundTrainers.size());
        assertEquals("Jane", foundTrainers.getFirst().getFirstName());
        assertEquals("Smith", foundTrainers.getFirst().getLastName());
    }

    @Test
    void testAdd() {
        Trainer newTrainer = new Trainer("Michael", "Johnson", "mikejohn", "password789", true, "PE", 3);
        trainerDAO.add(newTrainer);

        assertEquals(3, trainerMap.size());
        assertTrue(trainerMap.containsKey(3));
        assertEquals("Michael", trainerMap.get(3).getFirstName());
    }

    @Test
    void testUpdate() {
        Trainer updatedTrainer = new Trainer("John", "Doe", "johndoe", "newpassword", true, "PE", 1);
        trainerDAO.update(updatedTrainer);

        assertEquals("newpassword", trainerMap.get(1).getPassword());
    }

}