package com.tqs.polarent.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tqs.polarent.dto.LoginRequestDTO;
import com.tqs.polarent.dto.LoginResponseDTO;
import com.tqs.polarent.dto.RegisterRequestDTO;
import com.tqs.polarent.enums.Role;
import com.tqs.polarent.services.AuthService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private LoginRequestDTO loginRequest;
    private LoginResponseDTO loginResponse;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("john@test.com");
        loginRequest.setPassword("password123");

        loginResponse = LoginResponseDTO.builder()
                .userId(1L)
                .email("john@test.com")
                .firstName("John")
                .lastName("Doe")
                .role(Role.USER)
                .build();
    }

    @Test
    void login_ValidCredentials_ReturnsOk() throws Exception {
        when(authService.login(any(LoginRequestDTO.class))).thenReturn(loginResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.email", is("john@test.com")))
                .andExpect(jsonPath("$.role", is("USER")));
    }

    @Test
    void login_InvalidEmail_ReturnsNotFound() throws Exception {
        when(authService.login(any(LoginRequestDTO.class)))
                .thenThrow(new EntityNotFoundException("User not found"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void login_InvalidPassword_ReturnsUnauthorized() throws Exception {
        when(authService.login(any(LoginRequestDTO.class)))
                .thenThrow(new IllegalArgumentException("Invalid password"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_InactiveUser_ReturnsForbidden() throws Exception {
        when(authService.login(any(LoginRequestDTO.class)))
                .thenThrow(new IllegalStateException("User is inactive"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void login_MissingEmail_ReturnsBadRequest() throws Exception {
        loginRequest.setEmail(null);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_MissingPassword_ReturnsBadRequest() throws Exception {
        loginRequest.setPassword(null);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_ValidData_ReturnsCreated() throws Exception {
        RegisterRequestDTO registerRequest = new RegisterRequestDTO();
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setEmail("john@test.com");
        registerRequest.setPassword("password123");

        when(authService.register(any(RegisterRequestDTO.class))).thenReturn(loginResponse);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email", is("john@test.com")))
                .andExpect(jsonPath("$.role", is("USER")));
    }

    @Test
    void register_DuplicateEmail_ReturnsBadRequest() throws Exception {
        RegisterRequestDTO registerRequest = new RegisterRequestDTO();
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setEmail("john@test.com");
        registerRequest.setPassword("password123");

        when(authService.register(any(RegisterRequestDTO.class)))
                .thenThrow(new IllegalArgumentException("Email already exists"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest());
    }
}
