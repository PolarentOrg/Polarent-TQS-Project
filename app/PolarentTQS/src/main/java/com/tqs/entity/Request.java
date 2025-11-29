package com.tqs.entity;

public class Request {
    private int id;                         // unique id
    private int listingId;                  // id where this request "came from"
    private int requesterId,                // user who initiated this request
    private int initialDate;                // date when the requester wants to have the product from this request
    private int duration;                   // how many days the user will have to use the product
    private String note;                    // additional note the requester might have
}