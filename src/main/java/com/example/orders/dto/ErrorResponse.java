package com.example.orders.dto;

public record ErrorResponse(
        int status,
        String message
) {
}