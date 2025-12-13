package com.tqs.polarent.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tqs.polarent.dto.UserRequestDTO;
import com.tqs.polarent.dto.UserResponseDTO;
import com.tqs.polarent.entity.User;
import com.tqs.polarent.enums.Role;
import com.tqs.polarent.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserActivationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Clean database before each test
        userRepository.deleteAll();

        // Create a test user
        testUser = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@test.com")
                .password("password123")
                .role(Role.USER)
                .active(true)
                .build();
        testUser = userRepository.save(testUser);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    // ==================== ACTIVATION TESTS ====================

    @Test
    void whenActivateActiveUser_thenSuccess() throws Exception {
        assertThat(testUser.getActive()).isTrue();
        mockMvc.perform(patch("/api/users/{id}/activate", testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testUser.getId().intValue())))
                .andExpect(jsonPath("$.active", is(true)))
                .andExpect(jsonPath("$.email", is("john.doe@test.com")));
        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(updatedUser.getActive()).isTrue();
    }

    @Test
    void whenActivateInactiveUser_thenUserBecomesActive() throws Exception {
        testUser.setActive(false);
        userRepository.save(testUser);
        assertThat(testUser.getActive()).isFalse();

        mockMvc.perform(patch("/api/users/{id}/activate", testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testUser.getId().intValue())))
                .andExpect(jsonPath("$.active", is(true)))
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Doe")));

        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(updatedUser.getActive()).isTrue();
    }

    @Test
    void whenActivateNonExistentUser_thenNotFound() throws Exception {
        Long nonExistentId = 99999L;
        mockMvc.perform(patch("/api/users/{id}/activate", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("User not found")));
    }

    @Test
    void whenDeactivateActiveUser_thenUserBecomesInactive() throws Exception {
        assertThat(testUser.getActive()).isTrue();
        mockMvc.perform(patch("/api/users/{id}/deactivate", testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testUser.getId().intValue())))
                .andExpect(jsonPath("$.active", is(false)))
                .andExpect(jsonPath("$.email", is("john.doe@test.com")));
        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(updatedUser.getActive()).isFalse();
    }

    @Test
    void whenDeactivateInactiveUser_thenSuccess() throws Exception {
        testUser.setActive(false);
        userRepository.save(testUser);
        assertThat(testUser.getActive()).isFalse();

        mockMvc.perform(patch("/api/users/{id}/deactivate", testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testUser.getId().intValue())))
                .andExpect(jsonPath("$.active", is(false)));

        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(updatedUser.getActive()).isFalse();
    }

    @Test
    void whenDeactivateNonExistentUser_thenNotFound() throws Exception {
        Long nonExistentId = 99999L;
        mockMvc.perform(patch("/api/users/{id}/deactivate", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("User not found")));
    }

    @Test
    void whenInactiveUserTriesToLogin_thenForbidden() throws Exception {
        testUser.setActive(false);
        userRepository.save(testUser);
        String loginJson = """
                {
                    "email": "john.doe@test.com",
                    "password": "password123"
                }
                """;
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error", is("User is inactive")));
    }

    @Test
    void whenActiveUserLogsIn_thenSuccess() throws Exception {
        assertThat(testUser.getActive()).isTrue();
        String loginJson = """
                {
                    "email": "john.doe@test.com",
                    "password": "password123"
                }
                """;
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(testUser.getId().intValue())))
                .andExpect(jsonPath("$.email", is("john.doe@test.com")));
    }

    @Test
    void whenReactivatedUserLogsIn_thenSuccess() throws Exception {
        testUser.setActive(false);
        userRepository.save(testUser);

        mockMvc.perform(patch("/api/users/{id}/activate", testUser.getId()))
                .andExpect(status().isOk());
        String loginJson = """
                {
                    "email": "john.doe@test.com",
                    "password": "password123"
                }
                """;
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(testUser.getId().intValue())));
    }

    @Test
    void fullWorkflow_CreateDeactivateActivate() throws Exception {
        UserRequestDTO newUser = new UserRequestDTO();
        newUser.setFirstName("Jane");
        newUser.setLastName("Smith");
        newUser.setEmail("jane.smith@test.com");
        newUser.setPassword("secure123");
        newUser.setRole(Role.USER);

        String createResponse = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.active", is(true)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserResponseDTO created = objectMapper.readValue(createResponse, UserResponseDTO.class);
        Long userId = created.getId();
        mockMvc.perform(patch("/api/users/{id}/deactivate", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active", is(false)));
        String loginJson = String.format("""
                {
                    "email": "%s",
                    "password": "%s"
                }
                """, newUser.getEmail(), newUser.getPassword());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isForbidden());

        mockMvc.perform(patch("/api/users/{id}/activate", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active", is(true)));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is(newUser.getEmail())));
    }

    @Test
    void whenGetUserById_thenActiveStatusIsReturned() throws Exception {
        mockMvc.perform(get("/api/users/{id}", testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testUser.getId().intValue())))
                .andExpect(jsonPath("$.active", is(true)))
                .andExpect(jsonPath("$.email", is("john.doe@test.com")));
    }

    @Test
    void whenGetAllUsers_thenActiveStatusIsIncluded() throws Exception {
        User inactiveUser = User.builder()
                .firstName("Inactive")
                .lastName("User")
                .email("inactive@test.com")
                .password("password123")
                .role(Role.USER)
                .active(false)
                .build();
        userRepository.save(inactiveUser);
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].active").exists())
                .andExpect(jsonPath("$[1].active").exists());
    }
}