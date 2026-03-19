package com.theduckers.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;


//entity/UserPoints:



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

    protected UserPoints() { 
    }

    public UserPoints(Long userId) {
        this.userId = userId;
        this.balance = 0L;
        this.totalEarned = 0L;
        this.updatedAt = LocalDateTime.now();
    }


    public Long getUserId() {
        return userId;
    }

    public Long getBalance() {
        return balance;
    }

    public Long getTotalEarned() {
        return totalEarned;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public void setTotalEarned(Long totalEarned) {
        this.totalEarned = totalEarned;
    }

    public void touchUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }

    


}
