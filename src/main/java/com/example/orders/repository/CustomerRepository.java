package com.example.orders.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.orders.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("""
            select distinct c
            from Customer c
            left join fetch c.orders
            """)
    List<Customer> findAllWithOrders();
}