package com.example.orders.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.orders.dto.PageResponse;
import com.example.orders.dto.ProductRequest;
import com.example.orders.dto.ProductResponse;
import com.example.orders.entity.Product;
import com.example.orders.exception.ProductNotFoundException;
import com.example.orders.repository.ProductRepository;
import com.example.orders.specification.ProductSpecification;

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
    public PageResponse<ProductResponse> searchProductsByName(
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

    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> searchProductsWithSpecification(
            String name,
            Double minPrice,
            Double maxPrice,
            Pageable pageable) {

        Specification<Product> specification = null;

        if (name != null && !name.isBlank()) {
            specification = ProductSpecification.nameContains(name);
        }

        if (minPrice != null) {

            Specification<Product> priceSpecification =
                    ProductSpecification.priceGreaterThanOrEqual(minPrice);

            specification = specification == null
                    ? priceSpecification
                    : specification.and(priceSpecification);
        }

        if (maxPrice != null) {

            Specification<Product> priceSpecification =
                    ProductSpecification.priceLessThanOrEqual(maxPrice);

            specification = specification == null
                    ? priceSpecification
                    : specification.and(priceSpecification);
        }

        Page<Product> page = specification == null
                ? productRepository.findAll(pageable)
                : productRepository.findAll(specification, pageable);

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
    public ProductResponse updatePriceUsingDirtyChecking(
            Long id,
            Double price) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException(id));

        product.setPrice(price);

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