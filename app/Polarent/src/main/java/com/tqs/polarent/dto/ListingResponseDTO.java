package com.tqs.polarent.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ListingResponseDTO {
    private Long id;
    private Long ownerId;
    private String title;
    private String description;
    private Double dailyRate;
    private Boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}