package com.tqs.polarent.config;

import com.tqs.polarent.entity.User;
import com.tqs.polarent.enums.Role;
import com.tqs.polarent.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail("goni@goni.com")) {
            User admin = User.builder()
                    .firstName("Admin")
                    .lastName("User")
                    .email("goni@goni.com")
                    .password("goni5847")
                    .role(Role.ADMIN)
                    .active(true)
                    .build();
            userRepository.save(admin);
        }
    }
}
