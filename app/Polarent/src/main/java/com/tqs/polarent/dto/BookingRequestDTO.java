package com.tqs.polarent.dto;

import com.tqs.polarent.enums.Status;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookingRequestDTO {
    @NotNull(message = "Request ID is required")
    private Long requestId;

    @NotNull(message = "Price is required")
    private Double price;

    private Status status = Status.PENDING;
}