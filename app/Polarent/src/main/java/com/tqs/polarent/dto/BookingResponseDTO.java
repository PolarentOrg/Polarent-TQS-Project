package com.tqs.polarent.dto;

import com.tqs.polarent.enums.Status;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BookingResponseDTO {
    private Long id;
    private Long requestId;
    private Double price;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}