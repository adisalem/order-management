package com.example.orders.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.orders.dto.CustomerRequest;
import com.example.orders.dto.CustomerResponse;
import com.example.orders.service.CustomerService;

import jakarta.validation.Valid;

@RestController
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

@PostMapping("/customers")
@ResponseStatus(HttpStatus.CREATED)
public CustomerResponse createCustomer(
        @Valid @RequestBody CustomerRequest request) {

    return customerService.createCustomer(request);
}

@GetMapping("/customers/{id}")
public CustomerResponse getCustomer(@PathVariable Long id) {
    return customerService.getCustomer(id);
}

@PutMapping("/customers/{id}")
public CustomerResponse updateCustomer(
        @PathVariable Long id,
        @Valid @RequestBody CustomerRequest request) {

    return customerService.updateCustomer(id, request);
}

@DeleteMapping("/customers/{id}")
@ResponseStatus(HttpStatus.NO_CONTENT)
public void deleteCustomer(@PathVariable Long id) {
    customerService.deleteCustomer(id);
}

}