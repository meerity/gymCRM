package com.example.gymcrm.service;

import java.util.NoSuchElementException;
import com.example.gymcrm.entity.User;
import com.example.gymcrm.exception.InvalidPasswordException;
import com.example.gymcrm.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User with username " + username + " not found"));
    }

    public boolean checkLogin(String username, String password) throws NoSuchElementException {
        User user = getUserByUsername(username);
        return passwordEncoder.matches(password, user.getPassword());
    }

    public boolean userIsTrainer(String username) throws NoSuchElementException {
        User user = getUserByUsername(username);
        return user.getTrainer() != null;
    }

    public void toggleActive(String username) throws NoSuchElementException {
        User user = getUserByUsername(username);
        user.setActive(!user.getActive());
        userRepository.save(user);
    }

    public void changePassword(String username, String oldPassword, String newPassword) throws InvalidPasswordException {
        User user = getUserByUsername(username);
        if (passwordEncoder.matches(oldPassword, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        } else {
            throw new InvalidPasswordException("Invalid old password or username");
        }
    }
}
