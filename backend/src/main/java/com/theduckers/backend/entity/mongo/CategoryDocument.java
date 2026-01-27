package com.theduckers.backend.entity.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


//A diferencia de SQL, Mongo NO valida schema automáticamente
//Por tanto:
//Si el JSON tiene algo extra → no rompe, no obliga a declarar todos los campos tampoco
//Pero si el nombre de colección está mal → falla al arrancar


@Document(collection = "categories")
public class CategoryDocument {

    @Id
    private String id;

    private String name;
    private String description;

    protected CategoryDocument() {
    }

    // Getters & setters se agregarán después
}
