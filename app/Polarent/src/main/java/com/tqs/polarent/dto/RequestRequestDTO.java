package com.tqs.polarent.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class RequestRequestDTO {

    @NotNull(message = "Listing ID is required")
    private Long listingId;

    @NotNull(message = "Requester ID is required")
    private Long requesterId;

    @NotNull(message = "Initial date is required")
    @Positive(message = "Initial date must be positive")
    private Integer initialDate; // Changed from int to Integer for validation

    @NotNull(message = "Duration is required")
    @Positive(message = "Duration must be positive")
    private Integer duration; // Changed from int to Integer for validation

    private String note;
}