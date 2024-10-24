package com.example.gymcrm.controller;

import com.example.gymcrm.dto.TraineeDTO;
import com.example.gymcrm.dto.misc.UsernameAndPassword;
import com.example.gymcrm.dto.response.trainee.TraineeResponse;
import com.example.gymcrm.dto.response.trainee.TraineeResponseWithUsername;
import com.example.gymcrm.dto.response.trainer.FourFieldsTrainerResponse;
import com.example.gymcrm.dto.response.training.TrainingResponseForTrainee;
import com.example.gymcrm.dto.update.TraineeUpdateDTO;
import com.example.gymcrm.service.TraineeService;
import com.example.gymcrm.service.TrainingService;
import com.example.gymcrm.service.UserService;
import com.example.gymcrm.util.ControllerUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trainees")
@Tag(name = "Trainees management", description = "Trainee related endpoints, supports all CRUD operations")
public class TraineeController {

    private final TraineeService traineeService;
    private final TrainingService trainingService;
    private final UserService userService;

    @Operation(summary = "Add a new trainee", description = "Create a new trainee profile")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Trainee's username and password in response body and location header"),
        @ApiResponse(responseCode = "409", description = "Validation errors")
    })
    @PostMapping
    public ResponseEntity<String> addTrainee(@RequestBody @Valid TraineeDTO traineeDTO, Errors errors) {
        if (errors.hasErrors()) {
            return ControllerUtils.getValidationErrorsStringResponse(errors);
        }
        String firstName = traineeDTO.getFirstName();
        String lastName = traineeDTO.getLastName();
        LocalDate dateOfBirth = traineeDTO.getDateOfBirth();
        String address = traineeDTO.getAddress();

        UsernameAndPassword usernamePassword = traineeService.createProfile(firstName, lastName, dateOfBirth, address);

        String result = "Username: " + usernamePassword.getUsername() + "\nPassword: " + usernamePassword.getPassword();
        return ResponseEntity
                .created(URI.create("/api/trainees/" + usernamePassword.getUsername()))
                .body(result);
    }

    @Operation(summary = "Get a trainee", description = "Get a trainee profile by username")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Trainee profile"),
        @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @GetMapping("/{username}")
    public ResponseEntity<TraineeResponse> getTrainee(@PathVariable String username) {
        return ResponseEntity.ok(traineeService.getTraineeForResponseByUsername(username));
    }

    @Operation(summary = "Get trainee's trainings", description = "Get a list of trainings for a trainee by username")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of trainings"),
        @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @GetMapping("/{username}/trainings")
    public ResponseEntity<List<TrainingResponseForTrainee>> getTrainingsByTrainee(@PathVariable String username,
                                                                        @RequestParam(required = false) LocalDate dateFrom,
                                                                        @RequestParam(required = false) LocalDate dateTo,
                                                                        @RequestParam(required = false) String trainerFullname,
                                                                        @RequestParam(required = false) String typeName) {
        if (userService.userIsTrainer(username)) {
            return ResponseEntity.status(403).build();
        }
        List<TrainingResponseForTrainee> tResponse = trainingService.getTrainingsWithCriteriaForTrainee(username, dateFrom, dateTo, trainerFullname, typeName);
        return ResponseEntity.ok(tResponse);
    }

    @Operation(summary = "Update a trainee", description = "Update a trainee profile by username")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Trainee profile"),
        @ApiResponse(responseCode = "404", description = "Trainee not found"),
        @ApiResponse(responseCode = "409", description = "Validation errors")
    })
    @PutMapping("/{username}")
    public ResponseEntity<Object> updateTrainee(@PathVariable String username, 
                                                @RequestBody @Valid TraineeUpdateDTO traineeUpdateDTO, Errors errors) {
        if (errors.hasErrors()) {
            return ControllerUtils.getValidationErrorsMapResponse(errors);
        }

        String firstName = traineeUpdateDTO.getFirstName();
        String lastName = traineeUpdateDTO.getLastName();
        LocalDate dateOfBirth = traineeUpdateDTO.getDateOfBirth();
        String address = traineeUpdateDTO.getAddress();
        boolean isActive = traineeUpdateDTO.getIsActive();
        TraineeResponse response = traineeService.updateTrainee(username, firstName, lastName, dateOfBirth, address, isActive);

        return ResponseEntity.ok(new TraineeResponseWithUsername(username, response));
    }

    @Operation(summary = "Update trainee's trainers", description = "Update a list of trainers for a trainee by username")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of trainers"),
        @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @PutMapping("/{username}/trainers")
    public ResponseEntity<List<FourFieldsTrainerResponse>> updateTraineeTrainers(@PathVariable String username, @RequestBody List<String> trainerUsernames) {
        return ResponseEntity.ok(traineeService.updateTraineeTrainers(username, trainerUsernames));
    }

    @Operation(summary = "Toggle trainee's active status", description = "Toggle a trainee's active status by username")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Trainee's active status toggled"),
        @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @PatchMapping("/{username}/toggle-active")
    public ResponseEntity<String> toggleActive(@PathVariable String username) {
        userService.toggleActive(username);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Delete a trainee", description = "Delete a trainee profile by username")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Trainee deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @DeleteMapping("/{username}")
    public ResponseEntity<String> deleteTrainee(@PathVariable String username) {
        traineeService.deleteTrainee(username);
        return ResponseEntity.status(204).build();
    }
}
