package com.tqs.polarent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tqs.polarent.dto.BookingRequestDTO;
import com.tqs.polarent.entity.Booking;
import com.tqs.polarent.entity.Request;
import com.tqs.polarent.enums.Status;
import com.tqs.polarent.repository.BookingRepository;
import com.tqs.polarent.repository.RequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class BookingDeclineIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Request request;
    private Booking booking;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        requestRepository.deleteAll();

        request = Request.builder()
                .listingId(1L)
                .requesterId(2L)
                .initialDate(20251210)
                .duration(5)
                .build();
        request = requestRepository.save(request);

        booking = Booking.builder()
                .requestId(request.getId())
                .price(250.0)
                .status(Status.PENDING)
                .build();
        booking = bookingRepository.save(booking);
    }

    @Test
    void whenDeclineBooking_thenStatusShouldBeDeclined() throws Exception {
        mockMvc.perform(patch("/api/bookings/{id}/decline", booking.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(booking.getId()))
                .andExpect(jsonPath("$.status").value("DECLINED"));

        Booking updatedBooking = bookingRepository.findById(booking.getId()).orElseThrow();
        assertThat(updatedBooking.getStatus()).isEqualTo(Status.DECLINED);
    }

    @Test
    void whenDeclineNonExistentBooking_thenReturn400() throws Exception {
        mockMvc.perform(patch("/api/bookings/999/decline"))
                .andExpect(status().isBadRequest());
    }
}
