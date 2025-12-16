package com.tqs.polarent.dto;

import com.tqs.polarent.enums.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseDTO {
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;
}
