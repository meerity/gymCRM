package com.example.gymCRM.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Trainee extends User {
    private LocalDate dateOfBirth;
    private String address;
    private int userId;

    @JsonCreator
    public Trainee(@JsonProperty("firstName") String firstName,
                   @JsonProperty("lastName") String lastName,
                   @JsonProperty("username") String username,
                   @JsonProperty("password") String password,
                   @JsonProperty("isActive") boolean isActive,
                   @JsonProperty("dateOfBirth") LocalDate dateOfBirth,
                   @JsonProperty("address") String address,
                   @JsonProperty("userId") int userId) {
        super(firstName, lastName, username, password, isActive);
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Id: " + userId + super.toString() + ", Date of birth: " + dateOfBirth + ", Address: " + address;
    }
}
