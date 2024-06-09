package com.juny.spacestory.global.security.jwt.refresh;

import static com.juny.spacestory.global.security.jwt.refresh.QRefresh.refresh1;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomRefreshRepositoryImpl implements CustomRefreshRepository {

  private final JPAQueryFactory queryFactory;

  @Override
  @Transactional
  public int deleteExpiredTokens(String currentTime) {

    return (int) queryFactory.delete(refresh1).where(refresh1.expiration.lt(currentTime)).execute();
  }
}
