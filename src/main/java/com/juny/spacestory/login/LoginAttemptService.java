package com.juny.spacestory.login;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.juny.spacestory.global.exception.ErrorCode;
import com.juny.spacestory.global.exception.hierarchy.user.UserInvalidEmailException;
import com.juny.spacestory.user.repository.UserRepository;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Getter
@Slf4j
public class LoginAttemptService {

  private static final int MAX_ATTEMPTS = 3;

  private final UserRepository userRepository;

  private LoadingCache<String, Integer> loginAttemptCache;

  public LoginAttemptService(UserRepository userRepository) {
    this.userRepository = userRepository;
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

    userRepository.findByEmail(email).orElseThrow(
      () -> new UserInvalidEmailException(ErrorCode.USER_INVALID_EMAIL));

    int failedCount = 0;

    try {
      failedCount = loginAttemptCache.get(email);

    } catch (ExecutionException e) {

      log.error(e.getMessage());
      failedCount = 0;
    }

    ++failedCount;

    log.info("failedCount: " + failedCount);

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
