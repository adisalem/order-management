package com.example.orders;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.CannotAcquireLockException;

import com.example.orders.service.TransactionSerializableService;

@SpringBootTest
class TransactionSerializableTest {

    @Autowired
    private TransactionSerializableService serializableService;

    @Test
    void shouldDemonstrateSerializableIsolation()
            throws InterruptedException {

        Long productId =
                serializableService.createProduct();

        CountDownLatch start =
                new CountDownLatch(1);

        AtomicReference<Throwable> errorA =
                new AtomicReference<>();

        AtomicReference<Throwable> errorB =
                new AtomicReference<>();

        Thread transactionA = new Thread(() -> {

            try {
                start.await();

                serializableService.updateProduct(
                        productId,
                        1000
                );

            } catch (Throwable e) {
                errorA.set(e);
            }
        });

        Thread transactionB = new Thread(() -> {

            try {
                start.await();

                serializableService.updateProduct(
                        productId,
                        1000
                );

            } catch (Throwable e) {
                errorB.set(e);
            }
        });

        transactionA.start();
        transactionB.start();

        start.countDown();

        transactionA.join();
        transactionB.join();

        Throwable failureA = errorA.get();
        Throwable failureB = errorB.get();

        assertTrue(
                failureA instanceof CannotAcquireLockException
                        || failureB instanceof CannotAcquireLockException,
                "Expected one transaction to fail with CannotAcquireLockException"
        );
    }
}