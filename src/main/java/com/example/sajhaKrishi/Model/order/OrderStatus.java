package com.example.sajhaKrishi.Model.order;

public enum OrderStatus {
    PENDING,
    CONFIRMED,  // Replaces APPROVED
    PROCESSING,
    SHIPPED,    // Add this
    DELIVERED,  // Replaces COMPLETED
    CANCELLED   // Matches frontend (note: frontend uses double L)
}
