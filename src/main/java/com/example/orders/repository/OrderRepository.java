package com.example.orders.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.orders.dto.OrderSummary;
import com.example.orders.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("""
            select o
            from Order o
            where o.customer.id = :customerId
              and o.status = :status
            """)
    List<Order> findByCustomerAndStatus(
            @Param("customerId") Long customerId,
            @Param("status") String status
    );

    @Query("""
            select
                o.id as id,
                o.status as status,
                o.customer.id as customerId
            from Order o
            """)
    List<OrderSummary> findOrderSummaries();
}