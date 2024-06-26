package com.juny.spacestory.global.security.service;

import com.juny.spacestory.global.exception.ErrorCode;
import com.juny.spacestory.global.exception.hierarchy.user.UserInvalidEmailException;
import com.juny.spacestory.login.LoginAttemptService;
import com.juny.spacestory.user.domain.User;
import com.juny.spacestory.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;
  private final LoginAttemptService loginAttemptService;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new UserInvalidEmailException(ErrorCode.USER_INVALID_EMAIL));

    if (loginAttemptService.isBlocked(email)) {
      log.error("loadUserByUsername, Email is blocked.");

      throw new LockedException("user account is locked.");
    }

    if (user != null) {

      return new CustomUserDetails(user);
    }

    return null;
  }
}
