package com.theduckers.backend.dto.product;

import java.util.List;

/**
 * Public contract representation of a product.
 * Mirrors ProductDocument but decouples API layer from persistence layer.
 */




//dto/product/ProductResponse:


public class ProductResponse {

    private String id;
    private String name;
    private String description;
    private Long price;
    private Integer stock;
    private String categoryId;
    private List<String> images;

    public ProductResponse(
            String id,
            String name,
            String description,
            Long price,
            Integer stock,
            String categoryId,
            List<String> images
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.categoryId = categoryId;
        this.images = images;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Long getPrice() {
        return price;
    }

    public Integer getStock() {
        return stock;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public List<String> getImages() {
        return images;
    }
}