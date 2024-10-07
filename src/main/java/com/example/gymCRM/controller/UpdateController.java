package com.example.gymcrm.controller;

import java.util.List;
import com.example.gymcrm.dto.update.PasswordChangeDTO;
import com.example.gymcrm.dto.update.TraineeUpdateDTO;
import com.example.gymcrm.dto.update.TrainerUpdateDTO;
import com.example.gymcrm.service.TraineeService;
import com.example.gymcrm.service.TrainerService;
import com.example.gymcrm.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/update")
public class UpdateController {

    private final UserService userService;
    private final TraineeService traineeService;
    private final TrainerService trainerService;

    @PatchMapping("/password")
    public ResponseEntity<String> updatePassword(@RequestBody @Valid PasswordChangeDTO passwordDTO, Errors errors, Authentication authentication) {
        if (errors.hasErrors()) {
            return ControllerUtils.getValidationErrorsResponse(errors);
        }
        String username = authentication.getName();
        log.info(username);
        userService.changePassword(username, passwordDTO);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/trainer")
    public ResponseEntity<String> updateTrainer(@RequestBody @Valid TrainerUpdateDTO trainerUpdateDTO, Errors errors, Authentication authentication) {
        if (errors.hasErrors()) {
            return ControllerUtils.getValidationErrorsResponse(errors);
        }
        if (trainerUpdateDTO.areAllFieldsEmpty()) {
            return ResponseEntity.ok("Nothing was changed");
        } 
        else if (trainerUpdateDTO.getFirstName() != null || trainerUpdateDTO.getLastName() != null) {
            String username = trainerService.updateTrainer(authentication.getName(), trainerUpdateDTO);
            return ResponseEntity.ok("Username after update: " + username);
        } else {
            trainerService.updateTrainer(authentication.getName(), trainerUpdateDTO);
            return ResponseEntity.ok("Update succesfull. Username hasn`t changed");
        }

    }

    @PatchMapping("/trainee")
    public ResponseEntity<String> updateTrainee(@RequestBody @Valid TraineeUpdateDTO traineeUpdateDTO, Errors errors, Authentication authentication) {
        if (errors.hasErrors()) {
            return ControllerUtils.getValidationErrorsResponse(errors);
        }
        if (traineeUpdateDTO.areAllFieldsEmpty()) {
            return ResponseEntity.ok("Nothing was changed");
        }
        else if (traineeUpdateDTO.getFirstName() != null || traineeUpdateDTO.getLastName() != null) {
            String username = traineeService.updateTrainee(authentication.getName(), traineeUpdateDTO);
            return ResponseEntity.ok("Username after update: " + username);
        } else {
            traineeService.updateTrainee(authentication.getName(), traineeUpdateDTO);
            return ResponseEntity.ok("Update succesfull. Username hasn`t changed");
        }
    }

    @PatchMapping("/toggle-active")
    public ResponseEntity<String> updatePassword(Authentication authentication) {
        userService.toggleActive(authentication.getName());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/trainee/trainers")
    public ResponseEntity<String> updateTraineeTrainers(@RequestBody List<String> trainerUsernames, Authentication authentication) {
        String traineeUsername = authentication.getName();
        traineeService.updateTraineeTrainers(traineeUsername, trainerUsernames);
        return ResponseEntity.ok("Trainers list updated successfully");
    }

}
