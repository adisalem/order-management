package com.example.orders;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.orders.entity.Product;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@SpringBootTest
class ProductEntityLifecycleTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @Transactional
    void shouldDemonstrateDetachedEntity() {

        Product product = new Product();
        product.setName("Lifecycle Product");
        product.setPrice(100.0);

        System.out.println("Before persist: "
                + entityManager.contains(product));

        entityManager.persist(product);

        System.out.println("After persist: "
                + entityManager.contains(product));

        entityManager.flush();

        entityManager.detach(product);

        System.out.println("After detach: "
                + entityManager.contains(product));

        product.setPrice(9999.0);

        entityManager.flush();

        assertFalse(entityManager.contains(product));
        assertTrue(product.getPrice() == 9999.0);
    }

    @Test
@Transactional
void shouldMergeDetachedEntity() {

    Product product = new Product();
    product.setName("Merge Product");
    product.setPrice(100.0);

    entityManager.persist(product);
    entityManager.flush();

    entityManager.detach(product);

    product.setPrice(200.0);

    Product mergedProduct = entityManager.merge(product);

    System.out.println("Original managed: "
            + entityManager.contains(product));

    System.out.println("Merged managed: "
            + entityManager.contains(mergedProduct));

    entityManager.flush();

    System.out.println("Merged price: "
            + mergedProduct.getPrice());
}
}