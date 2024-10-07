package com.example.gymcrm.controller;

import com.example.gymcrm.dto.TrainingDTO;
import com.example.gymcrm.service.TrainingService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/add")
public class AddController {

    private final TrainingService trainingService;

    //Only permitted for trainers
    @PostMapping("/training")
    public ResponseEntity<String> postMethodName(@RequestBody @Valid TrainingDTO trainingDTO, Errors errors, Authentication authentication) {
        if (errors.hasErrors()) {
            return ControllerUtils.getValidationErrorsResponse(errors);
        }
        boolean isTrainer = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TRAINER"));
        if (!isTrainer) {
            return ResponseEntity.status(403).build();
        }
        trainingService.addTraining(trainingDTO);
        return ResponseEntity.ok("Successfully created new Training!");
    }
    

}
