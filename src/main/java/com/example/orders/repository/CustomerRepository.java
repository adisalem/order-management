package com.example.orders.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.orders.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}