package com.example.gymcrm.controller;

import com.example.gymcrm.dto.TraineeDTO;
import com.example.gymcrm.dto.TrainerDTO;
import com.example.gymcrm.service.TraineeService;
import com.example.gymcrm.service.TrainerService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;



@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/public")
public class PublicControler {

    private final TraineeService traineeService; 
    private final TrainerService trainerService; 

    @PostMapping("/add-trainer")
    public ResponseEntity<String> addTrainer(@RequestBody @Valid TrainerDTO trainerDTO, Errors errors) {
        if (errors.hasErrors()) {
            return ControllerUtils.getValidationErrorsResponse(errors);
        }
        Pair<String, String> usernamePassword = trainerService.createProfile(trainerDTO);
        String usernamePasswordString = "Username: " + usernamePassword.getLeft() + "\nPassword: " + usernamePassword.getRight();
        return ResponseEntity.ok(usernamePasswordString);
    }

    @PostMapping("/add-trainee")
    public ResponseEntity<String> addTrainee(@RequestBody @Valid TraineeDTO traineeDTO, Errors errors) {
        if (errors.hasErrors()) {
            return ControllerUtils.getValidationErrorsResponse(errors);
        }
        Pair<String, String> usernamePassword = traineeService.createProfile(traineeDTO);
        String usernamePasswordString = "Username: " + usernamePassword.getLeft() + "\nPassword: " + usernamePassword.getRight();
        return ResponseEntity.ok(usernamePasswordString);
    }

    
}
