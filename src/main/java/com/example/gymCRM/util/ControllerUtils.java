package com.example.gymcrm.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;

public class ControllerUtils {

    private ControllerUtils(){}

    public static ResponseEntity<String> getValidationErrorsStringResponse(Errors errors) {
        List<String> errorMessages = errors.getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .toList();
        String errorMessage = String.join("\n", errorMessages);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .body(errorMessage);
    }

    public static ResponseEntity<Object> getValidationErrorsMapResponse(Errors errors) {
        Map<String, String> errorMap = new HashMap<>();
        errors.getFieldErrors()
                .forEach(error -> errorMap.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMap);
    }
}
