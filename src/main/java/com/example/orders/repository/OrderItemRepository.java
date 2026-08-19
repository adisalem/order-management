package com.example.orders.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.orders.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}