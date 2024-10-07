package com.example.gymcrm.util;

import com.example.gymcrm.entity.User;
import com.example.gymcrm.repository.UserRepository;
import java.util.List;


public class UserUtils {

    private UserUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String createUserName(String firstName, String lastName, UserRepository userRepository) {
        List<User> trainersWithSameFullName = userRepository.findByFirstNameAndLastName(firstName, lastName);
        String username;
        if (trainersWithSameFullName.isEmpty()) {
            username = firstName + "." + lastName;
        } else {
            username = firstName + "." + lastName + trainersWithSameFullName.size();
        }
        return username;
    }
}