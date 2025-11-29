package com.tqs.enum;

public enum Status {
    PENDING("Waiting for payment"),
    PAID("Payment received"),
    ACCEPTED("Order accepted"),
    DECLINED("Order declined"),
    CANCELLED("Order cancelled"),
    COMPLETED("Order completed");
}