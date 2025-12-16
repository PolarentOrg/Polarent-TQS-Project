package com.tqs.polarent.controller;

import com.tqs.polarent.dto.ListingResponseDTO;
import com.tqs.polarent.services.ListingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ListingController.class)
class ListingControllerFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ListingService listingService;

    private ListingResponseDTO lisbonCamera;
    private ListingResponseDTO portoCamera;
    private ListingResponseDTO coimbraTripod;

    @BeforeEach
    void setUp() {
        lisbonCamera = new ListingResponseDTO();
        lisbonCamera.setId(1L);
        lisbonCamera.setTitle("Lisbon Camera");
        lisbonCamera.setDailyRate(89.99);
        lisbonCamera.setCity("Lisbon");
        lisbonCamera.setDistrict("Centro");

        portoCamera = new ListingResponseDTO();
        portoCamera.setId(2L);
        portoCamera.setTitle("Porto Camera");
        portoCamera.setDailyRate(59.99);
        portoCamera.setCity("Porto");
        portoCamera.setDistrict("Centro");

        coimbraTripod = new ListingResponseDTO();
        coimbraTripod.setId(3L);
        coimbraTripod.setTitle("Coimbra Tripod");
        coimbraTripod.setDailyRate(44.99);
        coimbraTripod.setCity("Coimbra");
        coimbraTripod.setDistrict("Coimbra Centro");
    }

    @Test
    void whenFilterByPriceRange_thenReturnOk() throws Exception {
        List<ListingResponseDTO> listings = Arrays.asList(portoCamera);
        when(listingService.filterByPriceRange(30.0, 100.0)).thenReturn(listings);

        mockMvc.perform(get("/api/listings/filter/price")
                        .param("min", "30")
                        .param("max", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].dailyRate").value(59.99));
    }

    @Test
    void whenFilterByPriceRangeWithMinOnly_thenReturnOk() throws Exception {
        List<ListingResponseDTO> listings = Arrays.asList(lisbonCamera, portoCamera);
        when(listingService.filterByPriceRange(50.0, null)).thenReturn(listings);

        mockMvc.perform(get("/api/listings/filter/price")
                        .param("min", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void whenFilterByPriceRangeWithMaxOnly_thenReturnOk() throws Exception {
        List<ListingResponseDTO> listings = Arrays.asList(coimbraTripod);
        when(listingService.filterByPriceRange(null, 50.0)).thenReturn(listings);

        mockMvc.perform(get("/api/listings/filter/price")
                            .param("max", "50"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].dailyRate").value(44.99));
    }

    @Test
    void whenFilterByPriceRangeNoParams_thenReturnAllEnabled() throws Exception {
        List<ListingResponseDTO> listings = Arrays.asList(lisbonCamera, portoCamera, coimbraTripod);
        when(listingService.filterByPriceRange(null, null)).thenReturn(listings);

        mockMvc.perform(get("/api/listings/filter/price"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    void whenFilterByMaxPrice_thenReturnOk() throws Exception {
        List<ListingResponseDTO> listings = Arrays.asList(coimbraTripod);
        when(listingService.filterByMaxPrice(50.0)).thenReturn(listings);

        mockMvc.perform(get("/api/listings/filter/max-price/50"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].dailyRate").value(44.99));
    }

    @Test
    void whenFilterByMinPrice_thenReturnOk() throws Exception {
        List<ListingResponseDTO> listings = Arrays.asList(lisbonCamera, portoCamera);
        when(listingService.filterByMinPrice(50.0)).thenReturn(listings);
        mockMvc.perform(get("/api/listings/filter/min-price/50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void whenFilterByCity_thenReturnOk() throws Exception {
        List<ListingResponseDTO> listings = Arrays.asList(lisbonCamera);
        when(listingService.filterByCity("Lisbon")).thenReturn(listings);

        mockMvc.perform(get("/api/listings/filter/city/Lisbon"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].city").value("Lisbon"));
    }

    @Test
    void whenSearchByCityPartialMatch_thenReturnOk() throws Exception {
        List<ListingResponseDTO> listings = Arrays.asList(lisbonCamera);
        when(listingService.searchByCity("lis")).thenReturn(listings);

        mockMvc.perform(get("/api/listings/search/city")
                            .param("city", "lis"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].city").value("Lisbon"));
    }

    @Test
    void whenFilterByDistrict_thenReturnOk() throws Exception {
        List<ListingResponseDTO> listings = Arrays.asList(lisbonCamera, portoCamera);
        when(listingService.filterByDistrict("Centro")).thenReturn(listings);

        mockMvc.perform(get("/api/listings/filter/district/Centro"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void whenFilterByPriceAndCity_thenReturnOk() throws Exception {
        List<ListingResponseDTO> listings = Arrays.asList(lisbonCamera);
        when(listingService.filterByPriceAndCity(50.0, 100.0, "Lisbon")).thenReturn(listings);

        mockMvc.perform(get("/api/listings/filter/price-city")
                            .param("min", "50")
                            .param("max", "100")
                            .param("city", "Lisbon"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].city").value("Lisbon"))
                    .andExpect(jsonPath("$[0].dailyRate").value(89.99));
    }

    @Test
    void whenFilterAdvanced_thenReturnOk() throws Exception {
        List<ListingResponseDTO> listings = Arrays.asList(lisbonCamera);
        when(listingService.filterAdvanced(50.0, 100.0, "Lisbon", "Centro")).thenReturn(listings);

        mockMvc.perform(get("/api/listings/filter/advanced")
                            .param("min", "50")
                            .param("max", "100")
                            .param("city", "Lisbon")
                            .param("district", "Centro"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].city").value("Lisbon"))
                    .andExpect(jsonPath("$[0].district").value("Centro"));
    }
    @Test
    void whenGetAllCities_thenReturnOk() throws Exception {
        List<String> cities = Arrays.asList("Coimbra", "Lisbon", "Porto");
        when(listingService.getAllCities()).thenReturn(cities);

        mockMvc.perform(get("/api/listings/cities"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0]").value("Coimbra"))
                    .andExpect(jsonPath("$[1]").value("Lisbon"))
                    .andExpect(jsonPath("$[2]").value("Porto"));
    }

    @Test
    void whenGetAllDistricts_thenReturnOk() throws Exception {
        List<String> districts = Arrays.asList("Alvalade", "Centro", "Coimbra Centro");
        when(listingService.getAllDistricts()).thenReturn(districts);

        mockMvc.perform(get("/api/listings/districts"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0]").value("Alvalade"))
                    .andExpect(jsonPath("$[1]").value("Centro"))
                    .andExpect(jsonPath("$[2]").value("Coimbra Centro"));
    }
    @Test
    void whenFilterWithInvalidPriceRange_thenReturnBadRequest() throws Exception {
        when(listingService.filterByPriceRange(100.0, 50.0))
                .thenThrow(new IllegalArgumentException("Minimum price cannot be greater than maximum price"));

        mockMvc.perform(get("/api/listings/filter/price")
                        .param("min", "100")
                        .param("max", "50"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenFilterWithInvalidCity_thenReturnEmptyList() throws Exception {
        when(listingService.filterByCity("InvalidCity")).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/listings/filter/city/InvalidCity"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}