package com.theduckers.backend.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;



//dto/category/CategoryResponse:

@Schema(description = "Public category representation")
public class CategoryResponse {

    @Schema(description = "Category identifier")
    private String id;

    @Schema(description = "Category name")
    private String name;

    @Schema(description = "Category description")
    private String description;

    public CategoryResponse(
            String id,
            String name,
            String description
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
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
}