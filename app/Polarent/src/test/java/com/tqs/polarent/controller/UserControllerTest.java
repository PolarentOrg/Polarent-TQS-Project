package com.tqs.polarent.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tqs.polarent.dto.UserRequestDTO;
import com.tqs.polarent.dto.UserResponseDTO;
import com.tqs.polarent.enums.Role;
import com.tqs.polarent.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserRequestDTO requestDTO;
    private UserResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new UserRequestDTO();
        requestDTO.setFirstName("John");
        requestDTO.setLastName("Doe");
        requestDTO.setEmail("john@test.com");
        requestDTO.setPassword("password123");
        requestDTO.setRole(Role.USER);

        responseDTO = new UserResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setFirstName("John");
        responseDTO.setLastName("Doe");
        responseDTO.setEmail("john@test.com");
        responseDTO.setRole(Role.USER);
        responseDTO.setActive(true);
    }

    @Test
    void createUser_WithUserRole_ReturnsCreated() throws Exception {
        when(userService.createUser(any(UserRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role", is("USER")));
    }

    @Test
    void createUser_WithAdminRole_ReturnsCreated() throws Exception {
        requestDTO.setRole(Role.ADMIN);
        responseDTO.setRole(Role.ADMIN);
        when(userService.createUser(any(UserRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role", is("ADMIN")));
    }

    @Test
    void getUserById_ReturnsUser() throws Exception {
        when(userService.getUserById(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role", is("USER")));
    }

    @Test
    void getUserByEmail_ReturnsUser() throws Exception {
        when(userService.getUserByEmail("john@test.com")).thenReturn(responseDTO);

        mockMvc.perform(get("/api/users/email/john@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("john@test.com")));
    }

    @Test
    void getAllUsers_ReturnsList() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].role", is("USER")));
    }

    @Test
    void deleteUser_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void activateUser_ReturnsUser() throws Exception {
        when(userService.activateUser(1L)).thenReturn(responseDTO);

        mockMvc.perform(patch("/api/users/1/activate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active", is(true)));
    }

    @Test
    void deactivateUser_ReturnsUser() throws Exception {
        responseDTO.setActive(false);
        when(userService.deactivateUser(1L)).thenReturn(responseDTO);

        mockMvc.perform(patch("/api/users/1/deactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active", is(false)));
    }
}
