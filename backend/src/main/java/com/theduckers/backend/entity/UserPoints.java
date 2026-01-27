package com.theduckers.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_points")
public class UserPoints {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false)
    private Long balance;

    @Column(name = "total_earned", nullable = false)
    private Long totalEarned;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected UserPoints() { //Constructor PROTEGIDO (requerido por JPA) y vacío por ahora, no sé porqué
    }

    // Getters & setters se agregarán después
}
