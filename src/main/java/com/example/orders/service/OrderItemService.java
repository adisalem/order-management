package com.example.orders.service;

import com.example.orders.dto.OrderItemRequest;
import com.example.orders.dto.OrderItemResponse;
import com.example.orders.entity.Order;
import com.example.orders.entity.OrderItem;
import com.example.orders.entity.Product;
import com.example.orders.exception.OrderItemNotFoundException;
import com.example.orders.exception.OrderNotFoundException;
import com.example.orders.exception.ProductNotFoundException;
import com.example.orders.repository.OrderItemRepository;
import com.example.orders.repository.OrderRepository;
import com.example.orders.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderItemService(
            OrderItemRepository orderItemRepository,
            OrderRepository orderRepository,
            ProductRepository productRepository) {

        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public OrderItemResponse createOrderItem(OrderItemRequest request) {

        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() ->
                        new OrderNotFoundException(request.orderId()));

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() ->
                        new ProductNotFoundException(request.productId()));

        OrderItem item = new OrderItem();

        item.setOrder(order);
        item.setProduct(product);
        item.setQuantity(request.quantity());

        OrderItem savedItem = orderItemRepository.save(item);

        return toResponse(savedItem);
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