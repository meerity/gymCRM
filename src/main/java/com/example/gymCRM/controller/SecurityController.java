package com.example.gymcrm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.gymcrm.dto.update.PasswordChangeDTO;
import com.example.gymcrm.service.UserService;
import com.example.gymcrm.util.ControllerUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@Tag(name = "Account management", description = "Security related endpoints")
public class SecurityController {

    private final UserService userService;

    @Operation(summary = "Login", description = "Check if given credentials are in system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful"),
        @ApiResponse(responseCode = "401", description = "Invalid username or password")
    })
    @GetMapping("/login")
    public ResponseEntity<String> login(@RequestParam @NotNull @NotEmpty String username,
                                        @RequestParam @NotNull @NotEmpty String password) {
        userService.checkLogin(username, password);
        return ResponseEntity.ok("Login successful");
    }

    @Operation(summary = "Update password", description = "Change user's password")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password updated successfully"),
        @ApiResponse(responseCode = "401", description = "Invalid old password or username"),
        @ApiResponse(responseCode = "409", description = "Validation errors")
    })
    @PutMapping("/password")
    public ResponseEntity<String> updatePassword(@RequestBody @Valid PasswordChangeDTO passwordChangeDTO, Errors errors) {
        if (errors.hasErrors()) {
            return ControllerUtils.getValidationErrorsStringResponse(errors);
        }
        userService.changePassword(passwordChangeDTO.getUsername(), passwordChangeDTO.getOldPassword(), passwordChangeDTO.getNewPassword());
        return ResponseEntity.ok("Password updated successfully");
    }

}
