package com.example.orders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.orders.repository.AuditRepository;
import com.example.orders.service.TransactionPropagationService;

@SpringBootTest
class TransactionPropagationTest {

    @Autowired
    private TransactionPropagationService transactionPropagationService;

    @Autowired
    private AuditRepository auditRepository;

    @Test
    void shouldCommitRequiresNewTransaction() {

        assertThrows(
                RuntimeException.class,
                () -> transactionPropagationService.outerTransaction()
        );

        assertEquals(1, auditRepository.count());
    }
}