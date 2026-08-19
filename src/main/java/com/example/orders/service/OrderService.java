package com.example.orders.service;

import com.example.orders.dto.OrderRequest;
import com.example.orders.entity.Customer;
import com.example.orders.entity.Order;
import com.example.orders.exception.CustomerNotFoundException;
import com.example.orders.repository.CustomerRepository;
import com.example.orders.repository.OrderRepository;
import org.springframework.stereotype.Service;

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

    public Order createOrder(OrderRequest request) {

        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() ->
                        new CustomerNotFoundException(request.customerId()));

        Order order = new Order();

        order.setStatus(request.status());
        order.setCustomer(customer);

        return orderRepository.save(order);
    }
}