package com.example.orders.dto;

public record OrderResponse(
        Long id,
        String status,
        Long customerId
) {
}