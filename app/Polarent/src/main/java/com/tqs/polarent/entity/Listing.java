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
    private Long id;                     // unique id

    @NotNull(message = "Owner ID is required")
    @Column(nullable = false)
    private Long ownerId;                // user who owns this listing

    @NotBlank(message = "Title is required")
    @Column(nullable = false)
    private String title;               // title

    @Column
    private String description;         // description

    @NotNull(message = "Daily rate is required")
    @Column(nullable = false)
    private Double dailyRate;           // amount the user has to pay per day

    @NotNull(message = "Visibility is required")
    @Column(nullable = false)
    private Boolean enabled;            // weather this listing is enabled or disabled (aka. visible or invisible)

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
