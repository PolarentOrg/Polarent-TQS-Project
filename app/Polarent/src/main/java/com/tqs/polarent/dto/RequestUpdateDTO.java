package com.tqs.polarent.dto;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class RequestUpdateDTO {
    @Positive(message = "Initial date must be positive")
    private Integer initialDate;

    @Positive(message = "Duration must be positive")
    private Integer duration;

    private String note;
}