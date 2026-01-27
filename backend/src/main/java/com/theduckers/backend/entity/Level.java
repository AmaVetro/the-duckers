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
}
