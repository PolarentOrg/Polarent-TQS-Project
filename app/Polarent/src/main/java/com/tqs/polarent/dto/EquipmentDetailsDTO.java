package com.tqs.polarent.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EquipmentDetailsDTO {
    private Long id;
    private String title;
    private String description;
    private Double dailyRate;
    private String city;
    private String district;
    private String ownerName;
    private String ownerEmail;
    private LocalDateTime createdAt;
    private Boolean available;
}
