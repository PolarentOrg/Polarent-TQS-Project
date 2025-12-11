package com.tqs.polarent.services;

import com.tqs.polarent.dto.LoginRequestDTO;
import com.tqs.polarent.dto.LoginResponseDTO;
import com.tqs.polarent.dto.RegisterRequestDTO;
import com.tqs.polarent.entity.User;
import com.tqs.polarent.enums.Role;
import com.tqs.polarent.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    private User user;
    private LoginRequestDTO loginRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@test.com")
                .password("password123")
                .role(Role.USER)
                .active(true)
                .build();

        loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("john@test.com");
        loginRequest.setPassword("password123");
    }

    @Test
    void login_ValidCredentials_ReturnsLoginResponse() {
        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(user));

        LoginResponseDTO response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals(1L, response.getUserId());
        assertEquals("john@test.com", response.getEmail());
        assertEquals("John", response.getFirstName());
        assertEquals(Role.USER, response.getRole());
    }

    @Test
    void login_InvalidEmail_ThrowsException() {
        when(userRepository.findByEmail("wrong@test.com")).thenReturn(Optional.empty());
        loginRequest.setEmail("wrong@test.com");

        assertThrows(EntityNotFoundException.class, () -> authService.login(loginRequest));
    }

    @Test
    void login_InvalidPassword_ThrowsException() {
        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(user));
        loginRequest.setPassword("wrongpassword");

        assertThrows(IllegalArgumentException.class, () -> authService.login(loginRequest));
    }

    @Test
    void login_InactiveUser_ThrowsException() {
        user.setActive(false);
        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(user));

        assertThrows(IllegalStateException.class, () -> authService.login(loginRequest));
    }

    @Test
    void login_AdminUser_ReturnsAdminRole() {
        user.setRole(Role.ADMIN);
        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(user));

        LoginResponseDTO response = authService.login(loginRequest);

        assertEquals(Role.ADMIN, response.getRole());
    }

    @Test
    void register_ValidData_ReturnsLoginResponse() {
        RegisterRequestDTO registerRequest = new RegisterRequestDTO();
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setEmail("john@test.com");
        registerRequest.setPassword("password123");

        when(userRepository.existsByEmail("john@test.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        LoginResponseDTO response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("john@test.com", response.getEmail());
        assertEquals(Role.USER, response.getRole());
    }

    @Test
    void register_DuplicateEmail_ThrowsException() {
        RegisterRequestDTO registerRequest = new RegisterRequestDTO();
        registerRequest.setEmail("john@test.com");

        when(userRepository.existsByEmail("john@test.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> authService.register(registerRequest));
    }
}
