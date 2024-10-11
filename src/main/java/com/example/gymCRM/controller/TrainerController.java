package com.example.gymcrm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.gymcrm.dto.TrainerDTO;
import com.example.gymcrm.dto.misc.UsernameAndPassword;
import com.example.gymcrm.dto.response.trainer.FourFieldsTrainerResponse;
import com.example.gymcrm.dto.response.trainer.TrainerResponse;
import com.example.gymcrm.dto.response.trainer.TrainerResponseWithUsername;
import com.example.gymcrm.dto.response.training.TrainingResponseForTrainer;
import com.example.gymcrm.dto.update.TrainerUpdateDTO;
import com.example.gymcrm.service.TrainerService;
import com.example.gymcrm.service.TrainingService;
import com.example.gymcrm.service.UserService;
import com.example.gymcrm.util.ControllerUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trainers")
@Tag(name = "Trainers Management", description = "Endpoints for managing trainer profiles, supports Create, Read and Update operations")
public class TrainerController {

    private final TrainerService trainerService;
    private final TrainingService trainingService;
    private final UserService userService;

    @Operation(summary = "Add a new trainer", description = "Create a new trainer profile")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Trainer's username and password in response body and location header"),
        @ApiResponse(responseCode = "409", description = "Validation errors")
    })
    @PostMapping
    public ResponseEntity<String> addTrainer(@RequestBody @Valid TrainerDTO trainerDTO, Errors errors) {
        if (errors.hasErrors()) {
            return ControllerUtils.getValidationErrorsStringResponse(errors);
        }
        String firstName = trainerDTO.getFirstName();
        String lastName = trainerDTO.getLastName();
        String specialization = trainerDTO.getSpecialization();
        UsernameAndPassword usernamePassword = trainerService.createProfile(firstName, lastName, specialization);
        String result = "Username: " + usernamePassword.getUsername() + "\nPassword: " + usernamePassword.getPassword();
        return ResponseEntity
                .created(URI.create("/api/trainers/" + usernamePassword.getUsername()))
                .body(result);
    }

    @Operation(summary = "Get a trainer", description = "Get a trainer profile by username")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Trainer profile"),
        @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    @GetMapping("/{username}")
    public ResponseEntity<TrainerResponse> getTrainer(@PathVariable String username) {
        return ResponseEntity.ok(trainerService.getTrainerForResponseByUsername(username));
    }

    @Operation(summary = "Get free trainers", description = "Get a list of free trainers for a trainee by trainee's username")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of free trainers"),
        @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @GetMapping("/free-trainers/{traineeUsername}")
    public ResponseEntity<List<FourFieldsTrainerResponse>> getNotAssignedTrainers(@PathVariable String traineeUsername) {
        List<FourFieldsTrainerResponse> freeTrainers = trainerService.getNotAssignedActiveTrainersByTraineeUsername(traineeUsername);
        return ResponseEntity.ok(freeTrainers);
    }

    @Operation(summary = "Get trainer's trainings", description = "Get a list of trainings for a trainer by trainer's username")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of trainings"),
        @ApiResponse(responseCode = "403", description = "User is not a trainer"),
        @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    @GetMapping("/{username}/trainings")
    public ResponseEntity<List<TrainingResponseForTrainer>> getTrainingsByTrainer(@PathVariable String username,
                                                                        @RequestParam(required = false) LocalDate dateFrom,
                                                                        @RequestParam(required = false) LocalDate dateTo,
                                                                        @RequestParam(required = false) String traineeFullname,
                                                                        @RequestParam(required = false) String typeName) {
        if (!userService.userIsTrainer(username)) {
            return ResponseEntity.status(403)
                    .header("Error", "This username belongs to Trainer")
                    .build();
        }
        List<TrainingResponseForTrainer> tResponse = trainingService.getTrainingsWithCriteriaForTrainer(username, dateFrom, dateTo, traineeFullname, typeName);
        return ResponseEntity.ok(tResponse);
    } 

    @Operation(summary = "Update a trainer", description = "Update a trainer profile by username")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Trainer profile"),
        @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    @PutMapping("/{username}")
    public ResponseEntity<Object> updateTrainer(@PathVariable String username, 
                                                @RequestBody @Valid TrainerUpdateDTO trainerUpdateDTO, Errors errors) {
        if (errors.hasErrors()) {
            return ControllerUtils.getValidationErrorsMapResponse(errors);
        }

        String firstName = trainerUpdateDTO.getFirstName();
        String lastName = trainerUpdateDTO.getLastName();
        String specialization = trainerUpdateDTO.getSpecialization();
        boolean isActive = trainerUpdateDTO.getActive();
        TrainerResponse response = trainerService.updateTrainer(username, firstName, lastName, specialization, isActive);
        
        return ResponseEntity.ok(new TrainerResponseWithUsername(username, response));
    }

    @Operation(summary = "Toggle trainer's active status", description = "Toggle a trainer's active status by username")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Trainer's active status toggled"),
        @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    @PatchMapping("/{username}/toggle-active")
    public ResponseEntity<String> toggleActive(@PathVariable String username) {
        userService.toggleActive(username);
        return ResponseEntity.ok().build();
    }
}
