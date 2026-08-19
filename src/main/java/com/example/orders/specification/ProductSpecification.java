package com.example.orders.specification;

import org.springframework.data.jpa.domain.Specification;

import com.example.orders.entity.Product;

public class ProductSpecification {

    public static Specification<Product> nameContains(String name) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.upper(root.get("name")),
                        "%" + name.toUpperCase() + "%"
                );
    }

    public static Specification<Product> priceGreaterThanOrEqual(
            Double minPrice) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(
                        root.get("price"),
                        minPrice
                );
    }

    public static Specification<Product> priceLessThanOrEqual(
            Double maxPrice) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(
                        root.get("price"),
                        maxPrice
                );
    }
}