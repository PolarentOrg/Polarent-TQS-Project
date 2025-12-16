package com.tqs.polarent;

import com.tqs.polarent.dto.ListingRequestDTO;
import com.tqs.polarent.entity.User;
import com.tqs.polarent.enums.Role;
import com.tqs.polarent.repository.ListingRepository;
import com.tqs.polarent.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ListingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ListingRepository listingRepository;

    private User owner;

    @BeforeEach
    void setUp() {
        listingRepository.deleteAll();
        userRepository.deleteAll();
        
        owner = userRepository.save(User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("owner@test.com")
                .password("password123")
                .role(Role.USER)
                .build());
    }

    @Test
    void createListing_Success() throws Exception {
        ListingRequestDTO dto = new ListingRequestDTO();
        dto.setOwnerId(owner.getId());
        dto.setTitle("Canon EOS R5");
        dto.setDescription("Professional camera");
        dto.setDailyRate(50.0);
        dto.setEnabled(true);

        mockMvc.perform(post("/api/listings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Canon EOS R5")))
                .andExpect(jsonPath("$.dailyRate", is(50.0)))
                .andExpect(jsonPath("$.ownerId", is(owner.getId().intValue())));
    }

    @Test
    void createListing_InvalidOwner() throws Exception {
        ListingRequestDTO dto = new ListingRequestDTO();
        dto.setOwnerId(999L);
        dto.setTitle("Canon EOS R5");
        dto.setDailyRate(50.0);
        dto.setEnabled(true);

        mockMvc.perform(post("/api/listings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createListing_MissingTitle() throws Exception {
        ListingRequestDTO dto = new ListingRequestDTO();
        dto.setOwnerId(owner.getId());
        dto.setDailyRate(50.0);
        dto.setEnabled(true);

        mockMvc.perform(post("/api/listings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getEnabledListings_OnlyReturnsEnabled() throws Exception {
        ListingRequestDTO dto1 = new ListingRequestDTO();
        dto1.setOwnerId(owner.getId());
        dto1.setTitle("Camera 1");
        dto1.setDailyRate(50.0);
        dto1.setEnabled(true);

        ListingRequestDTO dto2 = new ListingRequestDTO();
        dto2.setOwnerId(owner.getId());
        dto2.setTitle("Camera 2");
        dto2.setDailyRate(60.0);
        dto2.setEnabled(false);

        mockMvc.perform(post("/api/listings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto1)));

        mockMvc.perform(post("/api/listings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto2)));

        mockMvc.perform(get("/api/listings/enabled"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Camera 1")));
    }
}
