package com.theduckers.backend.dto.auth;

public class UserProfileResponse {

    private Long id;
    private String email;
    private String level;
    private long points;

    public UserProfileResponse(Long id, String email, String level, long points) {
        this.id = id;
        this.email = email;
        this.level = level;
        this.points = points;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getLevel() {
        return level;
    }

    public long getPoints() {
        return points;
    }
}
