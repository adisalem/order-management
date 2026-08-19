package com.example.orders.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.orders.dto.OrderItemResponse;
import com.example.orders.entity.OrderItem;
import com.example.orders.exception.OrderItemNotFoundException;
import com.example.orders.repository.OrderItemRepository;

@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;

    public OrderItemService(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    @Transactional(readOnly = true)
    public OrderItemResponse getOrderItem(Long id) {

        OrderItem item = orderItemRepository.findById(id)
                .orElseThrow(() ->
                        new OrderItemNotFoundException(id));

        return toResponse(item);
    }

    @Transactional(readOnly = true)
    public List<OrderItemResponse> getOrderItems() {

        return orderItemRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void deleteOrderItem(Long id) {

        OrderItem item = orderItemRepository.findById(id)
                .orElseThrow(() ->
                        new OrderItemNotFoundException(id));

        orderItemRepository.delete(item);
    }

    private OrderItemResponse toResponse(OrderItem item) {

        return new OrderItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getProduct().getPrice(),
                item.getQuantity(),
                item.getOrder().getId()
        );
    }
}