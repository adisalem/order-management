package com.example.orders.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.orders.dto.PageResponse;
import com.example.orders.dto.ProductRequest;
import com.example.orders.dto.ProductResponse;
import com.example.orders.entity.Product;
import com.example.orders.exception.ProductNotFoundException;
import com.example.orders.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {

        Product product = new Product();

        product.setName(request.name());
        product.setPrice(request.price());

        Product savedProduct = productRepository.save(product);

        return toResponse(savedProduct);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProduct(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException(id));

        return toResponse(product);
    }

    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> getProducts(Pageable pageable) {

        Page<Product> page = productRepository.findAll(pageable);

        return new PageResponse<>(
                page.getContent()
                        .stream()
                        .map(this::toResponse)
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> searchProducts(
            String name,
            Pageable pageable) {

        Page<Product> page =
                productRepository.findByNameContainingIgnoreCase(
                        name,
                        pageable
                );

        return new PageResponse<>(
                page.getContent()
                        .stream()
                        .map(this::toResponse)
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Transactional
    public ProductResponse updateProduct(
            Long id,
            ProductRequest request) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException(id));

        product.setName(request.name());
        product.setPrice(request.price());

        return toResponse(product);
    }

    @Transactional
    public void deleteProduct(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException(id));

        productRepository.delete(product);
    }

    private ProductResponse toResponse(Product product) {

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getVersion()
        );
    }
}