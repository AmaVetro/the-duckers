package com.theduckers.backend.controller;

import com.theduckers.backend.dto.category.CategoryResponse;
import com.theduckers.backend.entity.mongo.CategoryDocument;
import com.theduckers.backend.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;



//controller/CategoryController:

@RestController
@RequestMapping("/categories")
public class CategoryController {

        private final CategoryService categoryService;

        public CategoryController(CategoryService categoryService) {
                this.categoryService = categoryService;
        }

        // =========================================================
        // GET /categories
        // =========================================================
        @Operation(summary = "List all categories")
        @ApiResponses(value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Categories retrieved successfully",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = CategoryResponse.class)
                        )
                )
        })
        @GetMapping
        public ResponseEntity<List<CategoryResponse>> getAllCategories() {

                List<CategoryDocument> categories = categoryService.findAll();

                List<CategoryResponse> response = categories.stream()
                        .map(this::mapToResponse)
                        .toList();

                return ResponseEntity.ok(response);
        }

        // =========================================================
        // GET /categories/{id}
        // =========================================================
        @Operation(summary = "Get category by id")
        @ApiResponses(value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Category retrieved successfully",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = CategoryResponse.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Category not found",
                        content = @Content
                )
        })
        @GetMapping("/{id}")
        public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable String id) {

                return categoryService.findById(id)
                        .map(category -> ResponseEntity.ok(mapToResponse(category)))
                        .orElse(ResponseEntity.notFound().build());
        }

        private CategoryResponse mapToResponse(CategoryDocument document) {
                return new CategoryResponse(
                        document.getId(),
                        document.getName(),
                        document.getDescription()
                );
        }
}