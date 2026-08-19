package com.example.orders.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionPropagationService {

    private final AuditService auditService;

    public TransactionPropagationService(AuditService auditService) {
        this.auditService = auditService;
    }

    @Transactional
    public void outerTransaction() {

        auditService.saveAudit();

        throw new RuntimeException("Outer transaction failed");
    }
}