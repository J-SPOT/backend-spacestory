package com.juny.spacestory.global.scheduler;

import com.juny.spacestory.global.security.jwt.refresh.RefreshService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefreshDeleteScheduler {

  private final RefreshService refreshService;

  // 매일 5시 만료된 토큰 삭제 (토큰 만료일 시간이 기준 새벽 5시 전이라면 삭제)
  @Scheduled(cron = "0 0 5 * * *")
  public void RefreshDelete() {

    refreshService.deleteExpiredTokens();
  }
}
