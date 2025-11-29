package com.tqs.polarent.entity;

import com.tqs.polarent.enums.Status;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                             // unique id

    @NotBlank(message = "Request ID is required")
    @Column(nullable = false)
    private Long requestId;                      // request which "evolved" into a booking (aka. got accepted by the listing owner)

    @NotBlank(message = "Price is required")
    @Column(nullable = false)
    private Double price;                       // total price

    @NotBlank(message = "Status is required")
    @Column(nullable = false)
    private Status status = Status.PENDING;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
