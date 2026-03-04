package com.theduckers.backend.service;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import com.mongodb.client.result.UpdateResult;
import org.springframework.stereotype.Service;



//Service/StockService:



@Service
public class StockService {

    private final MongoTemplate mongoTemplate;

    public StockService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * Attempts to reserve stock atomically.
     *
     * Atomic operation:
     * Query:
     *   _id = productId
     *   AND stock >= quantity
     *
     * Update:
     *   $inc: { stock: -quantity }
     *
     * If modifiedCount == 1 → success
     * If modifiedCount == 0 → insufficient stock
     */
    public boolean reserveStock(String productId, int quantity) {

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        Query query = new Query(
                Criteria.where("_id").is(productId)
                        .and("stock").gte(quantity)
        );

        Update update = new Update().inc("stock", -quantity);

        UpdateResult result = mongoTemplate.updateFirst(
                query,
                update,
                "products"   // collection name
        );

        return result.getModifiedCount() == 1;
    }

    /**
     * Compensation method.
     * Used if SQL transaction fails after stock reservation.
     */
    public void restoreStock(String productId, int quantity) {

        if (quantity <= 0) {
            return;
        }

        Query query = new Query(
                Criteria.where("_id").is(productId)
        );

        Update update = new Update().inc("stock", quantity);

        mongoTemplate.updateFirst(
                query,
                update,
                "products"
        );
    }
}
