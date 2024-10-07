package com.example.gymcrm.controller;

import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import java.time.LocalDate;
import java.util.List;

import com.example.gymcrm.dto.response.TraineeResponse;
import com.example.gymcrm.dto.response.TrainerResponse;
import com.example.gymcrm.dto.response.TrainingResponse;
import com.example.gymcrm.service.TraineeService;
import com.example.gymcrm.service.TrainerService;
import com.example.gymcrm.service.TrainingService;
import com.example.gymcrm.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/get")
public class GetController {

    private final TrainerService trainerService;
    private final TraineeService traineeService;
    private final TrainingService trainingService;
    private final UserService userService;
    
    @GetMapping("/trainer/{username}")
    public ResponseEntity<TrainerResponse> getTrainer(@PathVariable String username) {
        return ResponseEntity.ok(trainerService.getTrainerForResponseByUsername(username));
    }

    @GetMapping("/trainee/{username}")
    public ResponseEntity<TraineeResponse> getTrainee(@PathVariable String username) {
        return ResponseEntity.ok(traineeService.getTraineeForResponseByUsername(username));
    }

    @GetMapping("/list-training/trainee/{traineeUsername}")
    public ResponseEntity<List<TrainingResponse>> getTrainingsByTrainee(@PathVariable String traineeUsername,
                                                                        @RequestParam(required = false) LocalDate dateFrom,
                                                                        @RequestParam(required = false) LocalDate dateTo,
                                                                        @RequestParam(required = false) String trainerFullname,
                                                                        @RequestParam(required = false) String typeName) {
        if (userService.userIsTrainer(traineeUsername)) {
            return ResponseEntity.status(403)
                    .header("Error", "This username belongs to Trainer")
                    .build();
        }
        List<TrainingResponse> tResponse = trainingService.getTrainingsWithCriteria(traineeUsername, dateFrom, dateTo, trainerFullname, typeName);
        return ResponseEntity.ok(tResponse);
    }

    @GetMapping("/list-training/trainer/{trainerUsername}")
    public ResponseEntity<List<TrainingResponse>> getTrainingsByTrainer(@PathVariable String trainerUsername,
                                                                        @RequestParam(required = false) LocalDate dateFrom,
                                                                        @RequestParam(required = false) LocalDate dateTo,
                                                                        @RequestParam(required = false) String traineeFullname,
                                                                        @RequestParam(required = false) String typeName) {
        if (!userService.userIsTrainer(trainerUsername)) {
            return ResponseEntity.status(403)
                    .header("Error", "This username belongs to Trainer")
                    .build();
        }
        List<TrainingResponse> tResponse = trainingService.getTrainingsWithCriteria(trainerUsername, dateFrom, dateTo, traineeFullname, typeName);
        return ResponseEntity.ok(tResponse);
    }

    @GetMapping("/free-trainers/{traineeUsername}")
    public ResponseEntity<List<TrainerResponse>> getMethodName(@PathVariable String traineeUsername) {
        List<TrainerResponse> freeTrainers = trainerService.getNotAssignedTrainersByTraineeUsername(traineeUsername);
        return ResponseEntity.ok(freeTrainers);
    }
    
}
