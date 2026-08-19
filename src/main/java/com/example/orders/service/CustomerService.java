package com.example.orders.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.orders.dto.CustomerRequest;
import com.example.orders.dto.CustomerResponse;
import com.example.orders.dto.OrderResponse;
import com.example.orders.entity.Customer;
import com.example.orders.exception.CustomerNotFoundException;
import com.example.orders.repository.CustomerRepository;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public CustomerResponse createCustomer(CustomerRequest request) {

        Customer customer =
                new Customer(request.name(), request.email());

        Customer savedCustomer = customerRepository.save(customer);

        return new CustomerResponse(
                savedCustomer.getId(),
                savedCustomer.getName(),
                savedCustomer.getEmail()
        );
    }

    @Transactional(readOnly = true)
    public CustomerResponse getCustomer(Long id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() ->
                        new CustomerNotFoundException(id));

        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getEmail()
        );
    }

    @Transactional
    public CustomerResponse updateCustomer(
            Long id,
            CustomerRequest request) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() ->
                        new CustomerNotFoundException(id));

        customer.setName(request.name());
        customer.setEmail(request.email());

        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getEmail()
        );
    }

    @Transactional
    public void deleteCustomer(Long id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() ->
                        new CustomerNotFoundException(id));

        customerRepository.delete(customer);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getCustomerOrders(Long id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() ->
                        new CustomerNotFoundException(id));

        return customer.getOrders()
                .stream()
                .map(order -> new OrderResponse(
                        order.getId(),
                        order.getStatus(),
                        order.getCustomer().getId()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> getCustomersWithOrdersUsingJoinFetch() {

        List<Customer> customers =
                customerRepository.findAllWithOrdersUsingJoinFetch();

        return customers.stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> getCustomersWithOrdersUsingEntityGraph() {

        List<Customer> customers =
                customerRepository.findAllWithOrdersUsingEntityGraph();

        return customers.stream()
                .map(this::toResponse)
                .toList();
    }

    private CustomerResponse toResponse(Customer customer) {

        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getEmail()
        );
    }
}