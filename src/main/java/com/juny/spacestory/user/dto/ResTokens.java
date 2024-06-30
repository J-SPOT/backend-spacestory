package com.juny.spacestory.user.dto;

public record ResTokens(String accessToken, String refreshToken, String accessTokenExpired,
                        String refreshTokenExpired) {
}
