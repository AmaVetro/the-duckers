package com.theduckers.backend.service;

import com.theduckers.backend.entity.mongo.CategoryDocument;
import com.theduckers.backend.repository.mongo.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


//service/CategoryService:

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryDocument> findAll() {
        return categoryRepository.findAll();
    }

    public Optional<CategoryDocument> findById(String id) {
        return categoryRepository.findById(id);
    }
}