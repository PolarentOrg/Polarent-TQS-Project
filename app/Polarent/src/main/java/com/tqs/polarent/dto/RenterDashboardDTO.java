package com.tqs.polarent.dto;

import lombok.Data;
import java.util.List;

@Data
public class RenterDashboardDTO {
    private Long userId;
    private String userName;
    private String userEmail;
    private List<DashboardRentalDTO> activeRentals;
    private List<DashboardRentalDTO> pendingRentals;
    private List<DashboardRentalDTO> completedRentals;
    private List<DashboardRentalDTO> cancelledRentals;
    private Integer totalRentals;
    private Double totalSpent;

    // Estatísticas
    private Integer activeRentalsCount;
    private Integer pendingRentalsCount;
    private Integer completedRentalsCount;
    private Integer cancelledRentalsCount;
    private Double monthlySpent;
    private Integer rentalsThisMonth;
}


