package com.example.gymcrm.dto.response.trainer;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonPropertyOrder({"username"})
public class TrainerResponseWithUsername extends TrainerResponse {
    private String username;

    public TrainerResponseWithUsername(String username, TrainerResponse response) {
        this.username = username;
        this.setFirstName(response.getFirstName());
        this.setLastName(response.getLastName());
        this.setSpecialization(response.getSpecialization());
        this.setActive(response.isActive());
        this.setTrainees(response.getTrainees());
    }
}
