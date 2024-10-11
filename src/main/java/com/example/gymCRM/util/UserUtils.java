package com.example.gymcrm.util;

import com.example.gymcrm.entity.User;
import com.example.gymcrm.repository.UserRepository;
import java.util.List;


public class UserUtils {

    private UserUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String createUserName(String firstName, String lastName, UserRepository userRepository) {
        String baseUsername = firstName + "." + lastName;
        List<User> usersWithSameUsername = userRepository.findByUsernameStartingWith(baseUsername);
        if (usersWithSameUsername.isEmpty()) {
            return baseUsername;
        }
        int number = usersWithSameUsername.stream()
                .map(User::getUsername)
                .map(username -> username.substring(baseUsername.length()))
                .filter(suffix -> suffix.matches("\\d+"))
                .mapToInt(Integer::parseInt)
                .max()
                .orElse(0) + 1;
        return baseUsername + number;
    }
}