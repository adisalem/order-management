package com.example.orders.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.orders.entity.Audit;

public interface AuditRepository extends JpaRepository<Audit, Long> {
}