package com.theduckers.backend.entity;

import jakarta.persistence.*;



@Entity
@Table(name = "levels")
public class Level {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    private String name;

    @Column(name = "min_points", nullable = false)
    private Long minPoints;

    protected Level() { //Constructor PROTEGIDO (requerido por JPA) y vacío por ahora, no sé porqué
    }




    // Getters & setters se agregarán después

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getMinPoints() {
        return minPoints;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Level)) return false;
        Level level = (Level) o;
        return id != null && id.equals(level.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }




}
