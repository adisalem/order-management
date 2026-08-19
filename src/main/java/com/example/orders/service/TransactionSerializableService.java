package com.example.orders.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.example.orders.entity.Product;
import com.example.orders.repository.ProductRepository;

@Service
public class TransactionSerializableService {

    private final ProductRepository productRepository;

    public TransactionSerializableService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public Long createProduct() {
        Product product = new Product();
        product.setName("Serializable Product");
        product.setPrice(100.0);

        return productRepository.save(product).getId();
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void updateProduct(Long productId, long delay) {

        Product product =
                productRepository.findById(productId).orElseThrow();

        double currentPrice = product.getPrice();

        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }

        product.setPrice(currentPrice + 10);
    }
}