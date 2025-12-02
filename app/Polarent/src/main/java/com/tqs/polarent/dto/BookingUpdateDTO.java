package com.tqs.polarent.dto;

import com.tqs.polarent.enums.Status;
import lombok.Data;

@Data
public class BookingUpdateDTO {
    private Double price;
    private Status status;
}