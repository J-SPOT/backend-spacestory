package com.juny.spacestory.global.security.jwt.refresh;

public interface CustomRefreshRepository {

  int deleteExpiredTokens(String currentTime);
}
