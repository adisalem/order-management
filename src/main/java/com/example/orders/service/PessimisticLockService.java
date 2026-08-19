package com.example.orders.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.orders.entity.Product;
import com.example.orders.repository.ProductRepository;

@Service
public class PessimisticLockService {

    private final ProductRepository productRepository;

    public PessimisticLockService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public Long createProduct() {
        Product product = new Product();
        product.setName("Pessimistic Product");
        product.setPrice(100.0);

        return productRepository.save(product).getId();
    }

    @Transactional
    public void updateProduct(Long productId, long delay) {

        Product product =
                productRepository.findByIdForUpdate(productId)
                        .orElseThrow();

        System.out.println(
                Thread.currentThread().getName()
                        + " acquired lock"
        );

        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }

        product.setPrice(product.getPrice() + 10);

        System.out.println(
                Thread.currentThread().getName()
                        + " releasing lock"
        );
    }
}