package com.theduckers.backend.entity.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;



//entity/mongo/ProductDocument:

@Document(collection = "products")
public class ProductDocument {

    @Id
    private String id;

    private String name;
    private String description;
    private Long price;
    private Integer stock;
    private String categoryId;
    private List<String> images;



    public ProductDocument() {
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

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }


    




    
}