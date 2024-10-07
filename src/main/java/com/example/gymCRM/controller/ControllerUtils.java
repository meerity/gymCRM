package com.example.gymcrm.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;

public class ControllerUtils {

    private ControllerUtils(){}

    public static ResponseEntity<String> getValidationErrorsResponse(Errors errors) {
        List<String> errorMessages = errors.getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .toList();
        return ResponseEntity.badRequest()
                             .header("Validation-Errors", String.join("; ", errorMessages))
                             .build();
    }

}
