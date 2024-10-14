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
import com.example.gymcrm.service.UserService;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class SecurityControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserService userService;

    @InjectMocks
    private SecurityController securityController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoginSuccess() {
        UsernamePasswordAuthenticationToken loginRequest = new UsernamePasswordAuthenticationToken("username", "password");
        Authentication authentication = new UsernamePasswordAuthenticationToken(loginRequest.getPrincipal(), loginRequest.getCredentials());
        
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);

        ResponseEntity<?> response = securityController.login(loginRequest.getPrincipal().toString(), loginRequest.getCredentials().toString());

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

}
