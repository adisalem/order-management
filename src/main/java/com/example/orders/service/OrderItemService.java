package com.example.orders.service;

import com.example.orders.dto.OrderItemRequest;
import com.example.orders.dto.OrderItemResponse;
import com.example.orders.entity.Order;
import com.example.orders.entity.OrderItem;
import com.example.orders.exception.OrderNotFoundException;
import com.example.orders.repository.OrderItemRepository;
import com.example.orders.repository.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;

    public OrderItemService(
            OrderItemRepository orderItemRepository,
            OrderRepository orderRepository) {

        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
    }

    public OrderItemResponse createOrderItem(OrderItemRequest request) {

        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() ->
                        new OrderNotFoundException(request.orderId()));

        OrderItem item = new OrderItem();

        item.setProduct(request.product());
        item.setQuantity(request.quantity());
        item.setOrder(order);

        OrderItem savedItem = orderItemRepository.save(item);

        return new OrderItemResponse(
                savedItem.getId(),
                savedItem.getProduct(),
                savedItem.getQuantity(),
                savedItem.getOrder().getId()
        );
    }
}