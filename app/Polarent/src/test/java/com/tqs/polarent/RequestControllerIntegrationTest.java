package com.tqs.polarent;

import com.tqs.polarent.dto.RequestResponseDTO;
import com.tqs.polarent.entity.Listing;
import com.tqs.polarent.entity.Request;
import com.tqs.polarent.entity.User;
import com.tqs.polarent.enums.Role;
import com.tqs.polarent.repository.BookingRepository;
import com.tqs.polarent.repository.ListingRepository;
import com.tqs.polarent.repository.RequestRepository;
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
class RequestControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User owner;
    private User requester;
    private Listing listing;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        requestRepository.deleteAll();
        listingRepository.deleteAll();
        userRepository.deleteAll();

        owner = userRepository.save(User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("owner@test.com")
                .password("password123")
                .role(Role.USER)
                .build());

        requester = userRepository.save(User.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("requester@test.com")
                .password("password123")
                .role(Role.USER)
                .build());

        listing = listingRepository.save(Listing.builder()
                .ownerId(owner.getId())
                .title("Canon EOS R5")
                .description("Professional camera")
                .dailyRate(50.0)
                .enabled(true)
                .build());
    }

    @Test
    void getRequestsByListing_Success() throws Exception {
        requestRepository.save(Request.builder()
                .listingId(listing.getId())
                .requesterId(requester.getId())
                .initialDate(20251215)
                .duration(3)
                .note("Need for event")
                .build());

        mockMvc.perform(get("/api/requests/listing/" + listing.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].listingId", is(listing.getId().intValue())))
                .andExpect(jsonPath("$[0].duration", is(3)));
    }

    @Test
    void getRequestsByListing_EmptyList() throws Exception {
        mockMvc.perform(get("/api/requests/listing/" + listing.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void convertToBooking_Success() throws Exception {
        Request request = requestRepository.save(Request.builder()
                .listingId(listing.getId())
                .requesterId(requester.getId())
                .initialDate(20251215)
                .duration(3)
                .build());

        RequestResponseDTO dto = new RequestResponseDTO();
        dto.setId(request.getId());
        dto.setListingId(listing.getId());
        dto.setRequesterId(requester.getId());
        dto.setDuration(3);

        mockMvc.perform(post("/api/requests/accept")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestId", is(request.getId().intValue())))
                .andExpect(jsonPath("$.price", is(150.0)));
    }

    @Test
    void convertToBooking_InvalidListing() throws Exception {
        RequestResponseDTO dto = new RequestResponseDTO();
        dto.setId(1L);
        dto.setListingId(999L);
        dto.setRequesterId(requester.getId());
        dto.setDuration(3);

        mockMvc.perform(post("/api/requests/accept")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}
