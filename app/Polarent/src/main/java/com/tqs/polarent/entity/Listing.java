package com.tqs.polarent.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "listings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Listing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Owner ID is required")
    @Column(nullable = false)
    private Long ownerId;

    @Column(nullable = false)
    private String title;

    private String description;

    @NotNull(message = "Daily rate is required")
    @Column(nullable = false)
    private Double dailyRate;

    @NotNull(message = "Visibility is required")
    @Column(nullable = false)
    private Boolean enabled;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

