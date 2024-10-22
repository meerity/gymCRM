package com.example.gymcrm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.gymcrm.dto.misc.UsernameAndPassword;
import com.example.gymcrm.dto.update.PasswordChangeDTO;
import com.example.gymcrm.service.LoginAttemptService;
import com.example.gymcrm.service.TokenBlacklistService;
import com.example.gymcrm.service.UserService;
import com.example.gymcrm.util.ControllerUtils;
import com.example.gymcrm.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;
    private final LoginAttemptService loginAttemptService;
    private final AuthenticationManager authenticationManager;

    @Operation(summary = "Login", description = "Check if given credentials are in system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "New JWT token in response body"),
        @ApiResponse(responseCode = "401", description = "Invalid username or password")
    })
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody @Valid UsernameAndPassword usernameAndPassword) {
        if (loginAttemptService.isBlocked(usernameAndPassword.getUsername())) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Account is locked. Try again after 5 minutes.");
        }

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(usernameAndPassword.getUsername(), usernameAndPassword.getPassword())
        );

        if (authentication.isAuthenticated()) {
            String token = jwtUtil.generateToken(usernameAndPassword.getUsername());
            return ResponseEntity.ok(Map.of("token", token));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    @Operation(summary = "Logout", description = "Invalidate JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token invalidated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid token")
    })
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            tokenBlacklistService.blacklistToken(token); 
            return ResponseEntity.ok("Logged out successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid token");
        }
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
