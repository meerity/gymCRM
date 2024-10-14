package com.example.gymcrm.service;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.gymcrm.dto.update.PasswordChangeDTO;
import com.example.gymcrm.entity.Trainee;
import com.example.gymcrm.entity.Trainer;
import com.example.gymcrm.entity.User;
import com.example.gymcrm.repository.UserRepository;

class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private UserService userService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUserIsTrainer() {
        User user = new User();
        user.setUsername("John.Doe");
        user.setTrainer(new Trainer());
        when(userRepository.findByUsername("John.Doe")).thenReturn(Optional.of(user));
        
        assertTrue(userService.userIsTrainer("John.Doe"));
        
        User nonTrainerUser = new User();
        nonTrainerUser.setUsername("Jane.Smith");
        nonTrainerUser.setTrainee(new Trainee());
        when(userRepository.findByUsername("Jane.Smith")).thenReturn(Optional.of(nonTrainerUser));
        
        assertFalse(userService.userIsTrainer("Jane.Smith"));
        
        verify(userRepository, times(2)).findByUsername(anyString());
    }
    
    @Test
    void testToggleActive() {
        User user = new User();
        user.setActive(true);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        
        userService.toggleActive("user");
        
        assertFalse(user.getActive());
        verify(userRepository).save(user);
    }
    
    @Test
    void testChangePassword() {
        User user = new User();
        user.setUsername("user");
        user.setPassword("encodedOldPassword");

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPassword", "encodedOldPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        PasswordChangeDTO passwordDTO = new PasswordChangeDTO();
        passwordDTO.setNewPassword("newPassword");
        
        userService.changePassword("user", "oldPassword", passwordDTO.getNewPassword());
        
        assertEquals("encodedNewPassword", user.getPassword());
        verify(userRepository).save(user);
    }
    
    @Test
    void testGetNotNullUserThrowsException() {
        when(userRepository.findByUsername("nonExistentUser")).thenReturn(Optional.empty());
        
        assertThrows(NoSuchElementException.class, () -> userService.userIsTrainer("nonExistentUser"));
    }
}
