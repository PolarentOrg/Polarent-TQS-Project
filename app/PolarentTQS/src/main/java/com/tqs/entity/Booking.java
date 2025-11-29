package com.tqs.entity;

public class Booking {
    private int id,                             // unique id
    private int requestId;                      // request which "evolved" into a booking (aka. got accepted by the listing owner)
    private double price;                       // total price
    private Status status = Status.PENDING;
}



private int id;                         // unique id
private int listingId;                  // id where this request "came from"
private int requesterId,                // user who initiated this request
private int initialDate;                // date when the requester wants to have the product from this request
private int duration;                   // how many days the user will have to use the product
private String note;                    // additional note the requester might have