package com.tqs.polarent.controller;

import com.tqs.polarent.dto.ListingResponseDTO;
import com.tqs.polarent.services.ListingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ListingController.class)
class ListingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ListingService listingService;

    @Test
    void getAllListings_ReturnsOk() throws Exception {
        ListingResponseDTO dto = new ListingResponseDTO();
        dto.setId(1L);
        dto.setTitle("Camera");
        when(listingService.getAllListings()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/listings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Camera"));
    }

    @Test
    void getListingsByOwner_ReturnsOk() throws Exception {
        ListingResponseDTO dto = new ListingResponseDTO();
        dto.setId(1L);
        dto.setOwnerId(5L);
        when(listingService.getListingsByOwner(5L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/listings/owner/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ownerId").value(5));
    }
}
