package com.learnwithiftekhar.jwtsecurity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

@Data
public class TokenPair {
    private String accessToken;
    private String refreshToken;

    public TokenPair(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
