package com.example.orders.dto;

public record ProductResponse(
        Long id,
        String name,
        Double price
) {
}