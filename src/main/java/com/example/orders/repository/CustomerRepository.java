package com.example.orders.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.orders.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Option 1: JPQL + JOIN FETCH
    @Query("""
            select distinct c
            from Customer c
            left join fetch c.orders
            """)
    List<Customer> findAllWithOrdersUsingJoinFetch();

    // Option 2: @EntityGraph
    @EntityGraph(attributePaths = "orders")
    @Query("select c from Customer c")
    List<Customer> findAllWithOrdersUsingEntityGraph();
}