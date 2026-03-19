package com.theduckers.backend.dto.product;

import java.util.List;
import java.util.Map;





//dto/product/ProductResponse:


public class ProductResponse {

    private String id;
    private String name;
    private String description;
    private Long price;
    private Integer stock;
    private String categoryId;
    private List<String> images;
    private Map<String, String> specs;

    public ProductResponse(
            String id,
            String name,
            String description,
            Long price,
            Integer stock,
            String categoryId,
            List<String> images,
            Map<String, String> specs
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.categoryId = categoryId;
        this.images = images;
        this.specs = specs;
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

    public Map<String, String> getSpecs() {
        return specs;
    }




}