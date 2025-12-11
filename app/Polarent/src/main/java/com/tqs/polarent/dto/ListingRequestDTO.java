package com.tqs.polarent.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ListingRequestDTO {

    @NotNull(message = "Owner ID is required")
    private Long ownerId;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Daily rate is required")
    @Positive(message = "Daily rate must be positive")
    private Double dailyRate;

    @NotNull(message = "Visibility is required")
    private Boolean enabled;

    private String city;
    private String district;
}