package com.theduckers.backend.entity.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

//A diferencia de SQL, Mongo NO valida schema automáticamente
//Por tanto:
//Si el JSON tiene algo extra → no rompe, no obliga a declarar todos los campos tampoco
//Pero si el nombre de colección está mal → falla al arrancar

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

    // createdAt se omite a propósito por ahora

    protected ProductDocument() {
    }

    // Getters & setters se agregarán después
}