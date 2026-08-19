package com.example.orders;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.orders.entity.Product;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.OptimisticLockException;

@SpringBootTest
class ProductOptimisticLockingTest {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    @SuppressWarnings("unused")
    private PlatformTransactionManager transactionManager;

    @Test
    void shouldDetectOptimisticLockConflict() {

        Long productId = createProduct();

        EntityManager entityManager1 =
                entityManagerFactory.createEntityManager();

        EntityManager entityManager2 =
                entityManagerFactory.createEntityManager();

        try {

            Product product1 =
                    entityManager1.find(Product.class, productId);

            Product product2 =
                    entityManager2.find(Product.class, productId);

            // Both transactions loaded version 0.
            System.out.println(
                    "Transaction 1 version: "
                            + product1.getVersion()
            );

            System.out.println(
                    "Transaction 2 version: "
                            + product2.getVersion()
            );

            // Transaction 1 updates first.
            entityManager1.getTransaction().begin();

            product1.setPrice(1100.0);

            entityManager1.getTransaction().commit();

            // Transaction 1 has now moved the database to version 1.
            System.out.println(
                    "Transaction 1 committed."
            );

            // Transaction 2 still has the old version 0.
            assertThrows(
                    OptimisticLockException.class,
                    () -> {

                        entityManager2.getTransaction().begin();

                        product2.setPrice(1200.0);

                        entityManager2.flush();

                        entityManager2.getTransaction().commit();
                    }
            );

            System.out.println(
                    "Optimistic locking conflict detected."
            );

        } finally {

            if (entityManager1.isOpen()) {
                entityManager1.close();
            }

            if (entityManager2.isOpen()) {
                entityManager2.close();
            }
        }
    }

    private Long createProduct() {

        EntityManager entityManager =
                entityManagerFactory.createEntityManager();

        try {

            entityManager.getTransaction().begin();

            Product product = new Product();

            product.setName("Laptop");
            product.setPrice(1000.0);

            entityManager.persist(product);

            entityManager.getTransaction().commit();

            return product.getId();

        } finally {

            entityManager.close();
        }
    }
}