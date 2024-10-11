package com.example.gymcrm.dto.response.trainer;

import java.util.List;
import com.example.gymcrm.dto.response.base.BaseTrainerResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TrainerResponse extends BaseTrainerResponse {
    private boolean active;
    private List<TraineeInfo> trainees;

    @Data
    public class TraineeInfo {
        private String username;
        private String firstName;
        private String lastName;
    }
}
