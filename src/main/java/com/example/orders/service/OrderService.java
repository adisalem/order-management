package com.example.orders.service;

import org.springframework.stereotype.Service;

import com.example.orders.dto.OrderRequest;
import com.example.orders.dto.OrderResponse;
import com.example.orders.entity.Customer;
import com.example.orders.entity.Order;
import com.example.orders.exception.CustomerNotFoundException;
import com.example.orders.repository.CustomerRepository;
import com.example.orders.repository.OrderRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;

    public OrderService(
            OrderRepository orderRepository,
            CustomerRepository customerRepository) {

        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
    }

    public OrderResponse createOrder(OrderRequest request) {

        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() ->
                        new CustomerNotFoundException(request.customerId()));

        Order order = new Order();
        order.setStatus(request.status());
        order.setCustomer(customer);

        Order savedOrder = orderRepository.save(order);

        return new OrderResponse(
                savedOrder.getId(),
                savedOrder.getStatus(),
                savedOrder.getCustomer().getId()
        );
    }

    public OrderResponse getOrder(Long id) {

    Order order = orderRepository.findById(id)
            .orElseThrow(() ->
                    new RuntimeException("Order with id " + id + " not found"));

    return new OrderResponse(
            order.getId(),
            order.getStatus(),
            order.getCustomer().getId()
    );
}
}