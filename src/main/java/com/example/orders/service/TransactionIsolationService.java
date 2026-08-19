package com.example.orders.service;

import java.util.concurrent.CountDownLatch;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.example.orders.entity.Product;
import com.example.orders.repository.ProductRepository;

@Service
public class TransactionIsolationService {

    private final ProductRepository productRepository;

    public TransactionIsolationService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public Long createProduct() {
        Product product = new Product();
        product.setName("Isolation Product");
        product.setPrice(100.0);

        return productRepository.save(product).getId();
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void readTwice(
            Long productId,
            CountDownLatch firstReadCompleted,
            CountDownLatch updateCompleted) {

        Product firstRead =
                productRepository.findById(productId).orElseThrow();

        System.out.println("Transaction A first read: "
                + firstRead.getPrice());

        firstReadCompleted.countDown();

        try {
            updateCompleted.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }

        Product secondRead =
                productRepository.findById(productId).orElseThrow();

        System.out.println("Transaction A second read: "
                + secondRead.getPrice());
    }

    @Transactional
    public void updateProduct(
            Long productId,
            CountDownLatch firstReadCompleted,
            CountDownLatch updateCompleted) {

        try {
            firstReadCompleted.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }

        Product product =
                productRepository.findById(productId).orElseThrow();

        product.setPrice(200.0);

        updateCompleted.countDown();

        System.out.println("Transaction B committed: 200.0");
    }
}