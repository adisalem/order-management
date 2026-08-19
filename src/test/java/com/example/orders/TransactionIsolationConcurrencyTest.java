package com.example.orders;

import java.util.concurrent.CountDownLatch;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.orders.service.TransactionIsolationService;

@SpringBootTest
class TransactionIsolationConcurrencyTest {

    @Autowired
    private TransactionIsolationService isolationService;

    @Test
    void shouldDemonstrateRepeatableRead()
            throws InterruptedException {

        Long productId =
                isolationService.createProduct();

        CountDownLatch firstReadCompleted =
                new CountDownLatch(1);

        CountDownLatch updateCompleted =
                new CountDownLatch(1);

        Thread transactionA = new Thread(() ->
                isolationService.readTwice(
                        productId,
                        firstReadCompleted,
                        updateCompleted
                )
        );

        Thread transactionB = new Thread(() ->
                isolationService.updateProduct(
                        productId,
                        firstReadCompleted,
                        updateCompleted
                )
        );

        transactionA.start();
        transactionB.start();

        transactionA.join();
        transactionB.join();
    }
}