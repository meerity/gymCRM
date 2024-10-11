package com.example.gymcrm.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.gymcrm.entity.User;
import com.example.gymcrm.repository.UserRepository;

class UserUtilsTest {

    @Mock
    private UserRepository userRepository;

    private UserUtils userUtils;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateUsername_FirstTimeUser() {
        when(userRepository.findByUsernameStartingWith(anyString())).thenReturn(new ArrayList<>());

        String username = UserUtils.createUserName("John", "Doe", userRepository);

        assertEquals("John.Doe", username);
        verify(userRepository, times(1)).findByUsernameStartingWith("John.Doe");
    }

    @Test
    void testGenerateUsername_UserExists() {
        User existingUser = new User();
        existingUser.setFirstName("John");
        existingUser.setLastName("Doe");
        existingUser.setUsername("John.Doe");

        when(userRepository.findByUsernameStartingWith("John.Doe")).thenReturn(List.of(existingUser));

        String username = UserUtils.createUserName("John", "Doe", userRepository);

        assertEquals("John.Doe1", username);
        verify(userRepository, times(1)).findByUsernameStartingWith("John.Doe");
    }
}


