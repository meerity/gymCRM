package com.example.gymcrm.dto.response.trainer;

import com.example.gymcrm.dto.response.base.BaseTrainerResponse;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonPropertyOrder({"username"})
public class FourFieldsTrainerResponse extends BaseTrainerResponse {
    private String username;
}
