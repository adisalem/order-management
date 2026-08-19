package com.example.orders;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.orders.service.PessimisticLockService;

@SpringBootTest
class PessimisticLockTest {

    @Autowired
    private PessimisticLockService pessimisticLockService;

    @Test
    void shouldSerializeConcurrentUpdates()
            throws InterruptedException {

        Long productId =
                pessimisticLockService.createProduct();

        Thread transactionA = new Thread(() ->
                pessimisticLockService.updateProduct(
                        productId,
                        2000
                ),
                "Transaction-A"
        );

        Thread transactionB = new Thread(() ->
                pessimisticLockService.updateProduct(
                        productId,
                        500
                ),
                "Transaction-B"
        );

        transactionA.start();

        Thread.sleep(200);

        transactionB.start();

        transactionA.join();
        transactionB.join();
    }
}