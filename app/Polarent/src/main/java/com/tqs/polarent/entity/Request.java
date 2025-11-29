package com.tqs.polarent.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Listing ID is required")
    @Column(nullable = false)
    private Long listingId;

    @NotNull(message = "Requester ID is required")
    @Column(nullable = false)
    private Long requesterId;

    @NotNull(message = "Initial date is required")
    @Positive(message = "Initial date must be positive")
    @Column(nullable = false)
    private Integer initialDate; // Changed from int to Integer for validation

    @NotNull(message = "Duration is required")
    @Positive(message = "Duration must be positive")
    @Column(nullable = false)
    private Integer duration; // Changed from int to Integer for validation

    @Column
    private String note;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}