package com.example.gymcrm.dto.response.trainee;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonPropertyOrder({"username"})
public class TraineeResponseWithUsername extends TraineeResponse {

    public TraineeResponseWithUsername(String username, TraineeResponse response) {
        this.username = username;
        this.setFirstName(response.getFirstName());
        this.setLastName(response.getLastName());
        this.setDateOfBirth(response.getDateOfBirth());
        this.setAddress(response.getAddress());
        this.setActive(response.isActive());
        this.setTrainers(response.getTrainers());
    }

    private String username;
}
