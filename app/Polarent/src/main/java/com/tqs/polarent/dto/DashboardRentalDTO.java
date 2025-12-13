package com.tqs.polarent.dto;

import com.tqs.polarent.enums.Status;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DashboardRentalDTO {
    private Long bookingId;
    private Long listingId;
    private String listingTitle;
    private Double price;
    private Status status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime bookingDate;
    private String ownerName;
    private String ownerEmail;
}
