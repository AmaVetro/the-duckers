package com.theduckers.backend.service;

import com.theduckers.backend.entity.mongo.ProductDocument;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


//service/ProductService:

@Service
public class ProductService {

    private final MongoTemplate mongoTemplate;

    public ProductService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    // =========================================================
    // FIND ALL WITH OPTIONAL FILTERS
    // =========================================================
    public List<ProductDocument> findAll(
            String text,
            String category,
            Long minPrice,
            Long maxPrice
    ) {

        Query query = new Query();
        List<Criteria> criteriaList = new ArrayList<>();

        // Text search (name contains, case-insensitive)
        if (text != null && !text.isBlank()) {
            criteriaList.add(
                Criteria.where("name")
                        .regex(text, "i")
            );
        }

        // Category filter
        if (category != null && !category.isBlank()) {
            criteriaList.add(
                Criteria.where("categoryId")
                        .is(category)
            );
        }

        // Price range filter
        if (minPrice != null || maxPrice != null) {

            Criteria priceCriteria = Criteria.where("price");

            if (minPrice != null) {
                priceCriteria = priceCriteria.gte(minPrice);
            }

            if (maxPrice != null) {
                priceCriteria = priceCriteria.lte(maxPrice);
            }

            criteriaList.add(priceCriteria);
        }

        if (!criteriaList.isEmpty()) {
            query.addCriteria(
                new Criteria().andOperator(
                        criteriaList.toArray(new Criteria[0])
                )
            );
        }

        return mongoTemplate.find(query, ProductDocument.class);
    }

    // =========================================================
    // FIND BY ID
    // =========================================================
    public Optional<ProductDocument> findById(String id) {
        return Optional.ofNullable(
                mongoTemplate.findById(id, ProductDocument.class)
        );
    }
}