package com.example.gymcrm.service;

import java.util.NoSuchElementException;
import com.example.gymcrm.dto.update.PasswordChangeDTO;
import com.example.gymcrm.entity.User;
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

    public boolean userIsTrainer(String username) throws NoSuchElementException {
        User user = getNotNullUser(username);
        return user.getTrainer() != null;
    }

    public void toggleActive(String username) throws NoSuchElementException {
        User user = getNotNullUser(username);
        user.setActive(!user.getActive());
        userRepository.save(user);
    }

    public void changePassword(String username, PasswordChangeDTO passwordDTO) throws NoSuchElementException {
        User user = getNotNullUser(username);
        String password = passwordDTO.getPassword();
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    private User getNotNullUser(String username) throws NoSuchElementException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User with username " + username + " not found"));
    }
}
