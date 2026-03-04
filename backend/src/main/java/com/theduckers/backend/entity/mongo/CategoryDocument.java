package com.theduckers.backend.entity.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


//A diferencia de SQL, Mongo NO valida schema automáticamente
//Por tanto:
//Si el JSON tiene algo extra → no rompe, no obliga a declarar todos los campos tampoco
//Pero si el nombre de colección está mal → falla al arrancar



//entity/mongo/CategoryDocument:

@Document(collection = "categories")
public class CategoryDocument {

    @Id
    private String id;

    private String name;
    private String description;

    protected CategoryDocument() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    

    
}
