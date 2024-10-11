package com.example.gymcrm.handler;

import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import com.example.gymcrm.exception.IllegalPasswordException;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ApiResponse(responseCode = "404", description = "Resource not found")
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Object> handleBadRequestExceptions(NoSuchElementException ex) {
        log.warn("Handled NoSuchElementException: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(ex.getMessage());
    }

    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    @ExceptionHandler(IllegalPasswordException.class)
    public ResponseEntity<Object> handleBadRequestExceptions(IllegalPasswordException ex) {
        log.warn("Handled IllegalArgumentException: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                             .body(ex.getMessage());
    }

    @ApiResponse(responseCode = "404", description = "Resource not found")
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Object> handleNotFoundExceptions(NoResourceFoundException ex) {
        log.warn("Handled NoResourceFoundException: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(ex.getMessage());
    }

    @ApiResponse(responseCode = "400", description = "Bad request")
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleNotFoundExceptions(HttpMessageNotReadableException ex){
        log.warn("Handled HttpMessageNotReadableException: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(ex.getMessage());
    }
}
