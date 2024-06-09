package com.juny.spacestory.global.security.jwt.refresh;

public record ResReissueTokens(
    String accessToken,
    String refreshToken,
    String accessTokenExpired,
    String refreshTokenExpired) {}
