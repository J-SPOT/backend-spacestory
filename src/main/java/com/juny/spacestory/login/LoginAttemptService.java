package com.juny.spacestory.login;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Getter
@Slf4j
public class LoginAttemptService {

  private static final int MAX_ATTEMPTS = 3;

  private LoadingCache<String, Integer> loginAttemptCache;

  public LoginAttemptService() {
    loginAttemptCache = CacheBuilder.newBuilder()
      .expireAfterWrite(1, TimeUnit.HOURS)
      .build(
        new CacheLoader<String, Integer>() {
          @Override
          public Integer load(final String key) {
            return 0;
          }
        });
  }

  public void loginSuccess(String email) {

    loginAttemptCache.invalidate(email);
  }

  public void loginFailed(String email) {

    int failedCount = 0;

    try {
      failedCount = loginAttemptCache.get(email);

    } catch (ExecutionException e) {

      log.error(e.getMessage());
      failedCount = 0;
    }

    ++failedCount;

    System.out.println("failedCount = " + failedCount);

    loginAttemptCache.put(email, failedCount);
  }

  public boolean isBlocked(String email) {

    try {
      return loginAttemptCache.get(email) >= MAX_ATTEMPTS;

    } catch (ExecutionException e){

      log.error(e.getMessage());
      return false;
    }
  }
}
