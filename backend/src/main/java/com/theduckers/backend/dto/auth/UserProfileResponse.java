package com.theduckers.backend.dto.auth;


//dto/auth/UserProfileResponse:


public class UserProfileResponse {

    private Long id;
    private String email;
    private String level;
    private long points;
    private String referralCode;

    public UserProfileResponse(Long id, String email, String level, long points, String referralCode) {
        this.id = id;
        this.email = email;
        this.level = level;
        this.points = points;
        this.referralCode = referralCode;
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

    public String getReferralCode() {
        return referralCode;
    }
}