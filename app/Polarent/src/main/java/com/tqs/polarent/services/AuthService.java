package com.tqs.polarent.services;

import com.tqs.polarent.dto.LoginRequestDTO;
import com.tqs.polarent.dto.LoginResponseDTO;
import com.tqs.polarent.dto.RegisterRequestDTO;
import com.tqs.polarent.entity.User;
import com.tqs.polarent.enums.Role;
import com.tqs.polarent.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public LoginResponseDTO login(LoginRequestDTO request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!user.getActive()) {
            throw new IllegalStateException("User is inactive");
        }

        if (!user.getPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        return buildLoginResponse(user);
    }

    public LoginResponseDTO register(RegisterRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(request.getPassword())
                .role(Role.USER)
                .active(true)
                .build();

        User saved = userRepository.save(user);
        return buildLoginResponse(saved);
    }

    private LoginResponseDTO buildLoginResponse(User user) {
        return LoginResponseDTO.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .build();
    }
}
