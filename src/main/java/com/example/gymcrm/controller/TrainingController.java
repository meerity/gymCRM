package com.example.gymcrm.controller;

import com.example.gymcrm.dto.TrainingDTO;
import com.example.gymcrm.service.TrainingService;
import com.example.gymcrm.util.ControllerUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDate;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/trainings")
@Tag(name = "Trainings management", description = "Training related endpoints, supports Create operation")
public class TrainingController {

    private final TrainingService trainingService;

    @Operation(summary = "Add a new training", description = "Create a new training")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Training created successfully"),
        @ApiResponse(responseCode = "409", description = "Validation errors")
    })
    @PostMapping
    public ResponseEntity<String> addTraining(@RequestBody @Valid TrainingDTO trainingDTO, Errors errors) {
        if (errors.hasErrors()) {
            return ControllerUtils.getValidationErrorsStringResponse(errors);
        }
        String traineeUsername = trainingDTO.getTraineeUsername();
        String trainerUsername = trainingDTO.getTrainerUsername();
        String trainingName = trainingDTO.getTrainingName();
        LocalDate trainingDate = trainingDTO.getTrainingDate();
        int duration = trainingDTO.getDuration();
        trainingService.addTraining(trainerUsername, traineeUsername, trainingName, trainingDate, duration);
        return ResponseEntity.ok("Successfully created new Training");
    }
    

}
