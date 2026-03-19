package com.theduckers.backend.entity;

import jakarta.persistence.*;



//entity/ProductDocument:


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

    protected Level() { 
    }

    private Level(Long id, String name, Long minPoints) {
        this.id = id;
        this.name = name;
        this.minPoints = minPoints;
    }





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


    public static Level defaultLevel() {
        return new Level(
                0L,
                "UNCONFIGURED",
                0L
        );
    }



}
