package com.example.gymCRM.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Trainer extends User {
    private String specialization;
    private int userId;

    @JsonCreator
    public Trainer(@JsonProperty("firstName") String firstName,
                   @JsonProperty("lastName") String lastName,
                   @JsonProperty("username") String username,
                   @JsonProperty("password") String password,
                   @JsonProperty("isActive") boolean isActive,
                   @JsonProperty("specialization") String specialization,
                   @JsonProperty("userId") int userId) {
        super(firstName, lastName, username, password, isActive);
        this.specialization = specialization;
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Id: " + userId + super.toString() + ", Specialization: " + specialization;
    }
}
