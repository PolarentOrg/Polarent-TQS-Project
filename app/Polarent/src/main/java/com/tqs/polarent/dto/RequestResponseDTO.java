package com.tqs.polarent.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RequestResponseDTO {
    private Long id;
    private Long listingId;
    private Long requesterId;
    private Integer initialDate;
    private Integer duration;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}