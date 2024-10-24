package com.example.gymcrm.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.Errors;
import com.example.gymcrm.service.UserService;
import com.example.gymcrm.service.LoginAttemptService;
import com.example.gymcrm.service.TokenBlacklistService;
import com.example.gymcrm.util.JwtUtil;
import com.example.gymcrm.dto.misc.UsernameAndPassword;
import com.example.gymcrm.dto.update.PasswordChangeDTO;
import jakarta.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @Mock
    private LoginAttemptService loginAttemptService;

    @InjectMocks
    private SecurityController securityController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoginSuccess() {
        UsernameAndPassword credentials = new UsernameAndPassword("testuser", "password");
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtUtil.generateToken(anyString())).thenReturn("test-token");

        ResponseEntity<Object> response = securityController.login(credentials);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof java.util.Map);
        assertEquals("test-token", ((java.util.Map<?, ?>)response.getBody()).get("token"));
    }

    @Test
    void testLoginFailure() {
        UsernameAndPassword credentials = new UsernameAndPassword("testuser", "wrongpassword");
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(false);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        ResponseEntity<Object> response = securityController.login(credentials);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid username or password", response.getBody());
    }

    @Test
    void testLogoutSuccess() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer test-token");

        ResponseEntity<String> response = securityController.logout(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Logged out successfully", response.getBody());
        verify(tokenBlacklistService).blacklistToken("test-token");
    }

    @Test
    void testLogoutFailure() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn(null);

        ResponseEntity<String> response = securityController.logout(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid token", response.getBody());
    }

    @Test
    void testUpdatePasswordSuccess() {
        PasswordChangeDTO passwordChangeDTO = new PasswordChangeDTO();
        passwordChangeDTO.setUsername("testuser");
        passwordChangeDTO.setOldPassword("oldpassword");
        passwordChangeDTO.setNewPassword("newpassword");

        Errors errors = mock(Errors.class);
        doNothing().when(userService).changePassword(anyString(), anyString(), anyString());

        ResponseEntity<String> response = securityController.updatePassword(passwordChangeDTO, errors);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Password updated successfully", response.getBody());
    }

    @Test
    void testUpdatePasswordFailure() {
        PasswordChangeDTO passwordChangeDTO = new PasswordChangeDTO();
        passwordChangeDTO.setUsername("testuser");
        passwordChangeDTO.setOldPassword("wrongoldpassword");
        passwordChangeDTO.setNewPassword("newpassword");

        Errors errors = mock(Errors.class);
        doThrow(new com.example.gymcrm.exception.InvalidPasswordException("Invalid old password or username"))
            .when(userService).changePassword(anyString(), anyString(), anyString());

        ResponseEntity<String> response = securityController.updatePassword(passwordChangeDTO, errors);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid old password or username", response.getBody());
    }
}
