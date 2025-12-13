package com.tqs.polarent.controller;

import com.tqs.polarent.dto.EquipmentDetailsDTO;
import com.tqs.polarent.services.ListingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ListingController.class)
class EquipmentDetailsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ListingService listingService;

    @Test
    void getEquipmentDetails_Success() throws Exception {
        EquipmentDetailsDTO details = new EquipmentDetailsDTO();
        details.setId(1L);
        details.setTitle("Canon EOS R5");
        details.setDescription("Professional camera");
        details.setDailyRate(50.0);
        details.setCity("Lisbon");
        details.setDistrict("Centro");
        details.setOwnerName("John Doe");
        details.setOwnerEmail("john@example.com");
        details.setCreatedAt(LocalDateTime.now());
        details.setAvailable(true);

        when(listingService.getEquipmentDetails(1L)).thenReturn(details);

        mockMvc.perform(get("/api/listings/1/details"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Canon EOS R5"))
                .andExpect(jsonPath("$.ownerName").value("John Doe"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void getEquipmentDetails_NotFound() throws Exception {
        when(listingService.getEquipmentDetails(999L))
                .thenThrow(new IllegalArgumentException("Equipment not found"));

        mockMvc.perform(get("/api/listings/999/details"))
                .andExpect(status().isBadRequest());
    }
}
