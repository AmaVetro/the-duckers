package com.theduckers.backend.repository.mongo;

import com.theduckers.backend.entity.mongo.CategoryDocument;
import org.springframework.data.mongodb.repository.MongoRepository;




public interface CategoryRepository extends MongoRepository<CategoryDocument, String> {
}
