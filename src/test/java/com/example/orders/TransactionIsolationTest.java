package com.example.orders;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.example.orders.entity.Product;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@SpringBootTest
class TransactionIsolationTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @Transactional(isolation = Isolation.READ_COMMITTED)
    void shouldDemonstrateReadCommitted() {

        Product product = new Product();
        product.setName("Isolation Product");
        product.setPrice(100.0);

        entityManager.persist(product);
        entityManager.flush();

        Product firstRead =
                entityManager.find(Product.class, product.getId());

        System.out.println("First read: "
                + firstRead.getPrice());

        System.out.println("Isolation level: READ_COMMITTED");
    }
}