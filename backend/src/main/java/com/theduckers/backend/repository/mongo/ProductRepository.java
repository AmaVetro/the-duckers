package com.theduckers.backend.repository.mongo;

import com.theduckers.backend.entity.mongo.ProductDocument;
import org.springframework.data.mongodb.repository.MongoRepository;



public interface ProductRepository extends MongoRepository<ProductDocument, String> {
}
