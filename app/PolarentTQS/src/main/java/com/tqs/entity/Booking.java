package com.tqs.entity;

public class Booking {
    private int id,                             // unique id
    private int requestId;                      // request which "evolved" into a booking (aka. got accepted by the listing owner)
    private double price;                       // total price
    private Status status = Status.PENDING;
}
