package com.example.orders.dto;

public record OrderItemResponse(
        Long id,
        Long productId,
        String productName,
        Double productPrice,
        Integer quantity,
        Long orderId
) {
}