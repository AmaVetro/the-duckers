package com.theduckers.backend.controller;

import com.theduckers.backend.dto.product.ProductResponse;
import com.theduckers.backend.entity.mongo.ProductDocument;
import com.theduckers.backend.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;



//controller/ProductController:

@RestController
@RequestMapping("/products")
public class ProductController {

        private final ProductService productService;

        public ProductController(ProductService productService) {
                this.productService = productService;
        }

        // =========================================================
        // GET /products (with optional filters)
        // =========================================================
        @Operation(
                summary = "List products with optional filters",
                description = """
                        Returns products from catalog.
                        Optional filters:
                        - text (name search)
                        - category
                        - minPrice
                        - maxPrice
                        """
        )
        @ApiResponses({
                @ApiResponse(
                        responseCode = "200",
                        description = "Products retrieved successfully",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ProductResponse.class)
                        )
                )
        })
        @GetMapping
        public ResponseEntity<List<ProductResponse>> getAllProducts(
                @RequestParam(required = false) String text,
                @RequestParam(required = false) String category,
                @RequestParam(required = false) Long minPrice,
                @RequestParam(required = false) Long maxPrice
        ) {

                List<ProductResponse> products = productService.findAll(
                                text,
                                category,
                                minPrice,
                                maxPrice
                        )
                        .stream()
                        .map(this::mapToResponse)
                        .toList();

                return ResponseEntity.ok(products);
        }

        // =========================================================
        // GET /products/{id}
        // =========================================================
        @Operation(
                summary = "Get product detail",
                description = "Returns a single product by its MongoDB identifier."
        )
        @ApiResponses({
                @ApiResponse(
                        responseCode = "200",
                        description = "Product retrieved successfully",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ProductResponse.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Product not found",
                        content = @Content
                )
        })
        @GetMapping("/{id}")
        public ResponseEntity<ProductResponse> getProductById(@PathVariable String id) {

                return productService.findById(id)
                        .map(this::mapToResponse)
                        .map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
        }

        private ProductResponse mapToResponse(ProductDocument doc) {
                return new ProductResponse(
                        doc.getId(),
                        doc.getName(),
                        doc.getDescription(),
                        doc.getPrice(),
                        doc.getStock(),
                        doc.getCategoryId(),
                        doc.getImages()
                );
        }
}