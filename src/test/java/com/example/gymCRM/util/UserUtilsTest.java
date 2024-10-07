package com.example.gymcrm.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.gymcrm.entity.User;
import com.example.gymcrm.repository.UserRepository;

class UserUtilsTest {

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUserName_NoExistingUsers() {
        when(userRepository.findByFirstNameAndLastName("John", "Doe")).thenReturn(new ArrayList<>());

        String username = UserUtils.createUserName("John", "Doe", userRepository);

        assertEquals("John.Doe", username);
        verify(userRepository).findByFirstNameAndLastName("John", "Doe");
    }

    @Test
    void testCreateUserName_ExistingUsers() {
        List<User> existingUsers = new ArrayList<>();
        existingUsers.add(new User());
        existingUsers.add(new User());

        when(userRepository.findByFirstNameAndLastName("Jane", "Smith")).thenReturn(existingUsers);

        String username = UserUtils.createUserName("Jane", "Smith", userRepository);

        assertEquals("Jane.Smith2", username);
        verify(userRepository).findByFirstNameAndLastName("Jane", "Smith");
    }

    @Test
    void testCreateUserName_MultipleExistingUsers() {
        List<User> existingUsers = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            existingUsers.add(new User());
        }

        when(userRepository.findByFirstNameAndLastName("Robert", "Johnson")).thenReturn(existingUsers);

        String username = UserUtils.createUserName("Robert", "Johnson", userRepository);

        assertEquals("Robert.Johnson5", username);
        verify(userRepository).findByFirstNameAndLastName("Robert", "Johnson");
    }
}


