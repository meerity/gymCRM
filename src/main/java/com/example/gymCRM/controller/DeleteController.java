package com.example.gymcrm.controller;

import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.example.gymcrm.entity.Trainee;
import com.example.gymcrm.service.TraineeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/delete")

public class DeleteController {

    private final TraineeService traineeService;

    @DeleteMapping("/trainee/{username}")
    public ResponseEntity<Trainee> deleteTrainee(@PathVariable String username, Authentication authentication) {
        boolean isTrainer = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TRAINER"));

        if (isTrainer) {
            traineeService.deleteTrainee(username);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(403).build();
        }
        
    }
    

}
