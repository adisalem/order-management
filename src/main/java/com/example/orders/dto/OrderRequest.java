package com.example.orders.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrderRequest(

        @NotBlank(message = "Status is required")
        String status,

        @NotNull(message = "Customer ID is required")
        Long customerId

) {
}