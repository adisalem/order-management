package com.example.orders.dto;

public record OrderItemResponse(
        Long id,
        String product,
        Integer quantity,
        Long orderId
) {
}