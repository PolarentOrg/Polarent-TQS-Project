package com.tqs.polarent.dto;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ListingUpdateDTO {
    private String title;
    private String description;

    @Positive(message = "Daily rate must be positive")
    private Double dailyRate;

    private Boolean enabled;
}