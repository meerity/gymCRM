package com.example.gymCRM.entity;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public abstract class User {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private boolean isActive;

    public User(String firstName, String lastName, String username, String password, boolean isActive) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        String activeness = isActive ? "active" : "inactive";
        return ", " + firstName + " " + lastName + ", Username: " + username + ", Password: " + password + ", Status: " + activeness;
    }

}
