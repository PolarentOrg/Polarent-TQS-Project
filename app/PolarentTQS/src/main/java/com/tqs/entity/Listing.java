package com.tqs.entity;

public class Listing {
    private int id;                     // unique id
    private int ownerId;                // user who owns this listing
    private String title;               // title
    private String description;         // description
    private double dailyRate;           // amount the user has to pay per day
    private boolean enabled;            // wether this listing is enabled or disabled (aka. visible or invisble)
}