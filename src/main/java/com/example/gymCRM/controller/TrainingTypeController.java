package com.example.gymcrm.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.gymcrm.dto.response.trainingtype.TrainingTypeResponse;
import com.example.gymcrm.service.TrainingTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/training-types")
@Tag(name = "Training types catalog", description = "Training type related endpoints, supports only Read operation")
public class TrainingTypeController {

    private final TrainingTypeService trainingTypeService;

    @Operation(summary = "Get all training types", description = "Get a list of all training types")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of training types")
    })
    @GetMapping
    public ResponseEntity<List<TrainingTypeResponse>> getTrainingTypes() {
        return ResponseEntity.ok(trainingTypeService.getTrainingTypes());
    }

}
