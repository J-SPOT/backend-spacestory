package com.juny.spacestory.user.service;

import com.juny.spacestory.global.exception.ErrorCode;
import com.juny.spacestory.global.exception.hierarchy.parameter.ParameterIsNullOrEmpty;
import com.juny.spacestory.global.exception.hierarchy.token.RefreshTokenInvalidException;
import com.juny.spacestory.global.exception.hierarchy.user.UserInvalidEmailException;
import com.juny.spacestory.global.security.jwt.JwtUtil;
import com.juny.spacestory.global.security.jwt.refresh.Refresh;
import com.juny.spacestory.global.security.jwt.refresh.RefreshRepository;
import com.juny.spacestory.user.domain.User;
import com.juny.spacestory.user.dto.ResTokens;
import com.juny.spacestory.user.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TwoFactorAuthService {

  private final EmailVerificationService emailVerificationService;
  private final TotpVerificationService totpVerificationService;
  private final UserService userService;
  private final UserRepository userRepository;
  private final RefreshRepository refreshRepository;
  private final JwtUtil jwtUtil;

  private final String REFRESH_TOKEN_NULL_OR_EMPTY_MSG = "Refresh token is null or empty.";

  public ResTokens verifyEmailCodeAndIssueTokens(String refreshToken, String code, String ip, boolean isRegister) {

    validateRefreshToken(refreshToken);

    String uuid = jwtUtil.getId(refreshToken);

    User user = userRepository.findById(UUID.fromString(uuid)).orElseThrow(
      () -> new UserInvalidEmailException(ErrorCode.USER_INVALID_EMAIL));

    emailVerificationService.verifyCode(user.getEmail(), code, isRegister);

    userService.addIpAddress(user, ip);

    return getIssuedTokens(user);
  }

  public ResTokens verifyTotpCodeAndIssueTokens(String refreshToken, Integer code, String ip) {

    validateRefreshToken(refreshToken);

    String uuid = jwtUtil.getId(refreshToken);

    User user = userRepository.findById(UUID.fromString(uuid)).orElseThrow(
      () -> new UserInvalidEmailException(ErrorCode.USER_INVALID_EMAIL));

    totpVerificationService.verifyTotpCode(user.getEmail(), code);

    userService.addIpAddress(user, ip);

    return getIssuedTokens(user);
  }

  private ResTokens getIssuedTokens(User user) {
    String accessToken = jwtUtil.createJwt(jwtUtil.ACCESS_TOKEN_PREFIX, user.getId().toString(),
      user.getRole().toString());
    String refreshToken = jwtUtil.createJwt(jwtUtil.REFRESH_TOKEN_PREFIX, user.getId().toString(),
      user.getRole().toString());

    String accessTokenExpired =
      jwtUtil.convertDateToLocalDateTime(jwtUtil.getExpiration(accessToken));
    String refreshTokenExpired =
      jwtUtil.convertDateToLocalDateTime(jwtUtil.getExpiration(refreshToken));

    refreshRepository.save(new Refresh(user.getId(), refreshToken, refreshTokenExpired));

    return new ResTokens(accessToken, refreshToken, accessTokenExpired, refreshTokenExpired);
  }

  private void validateRefreshToken(String refreshToken) {
    if (refreshToken == null || refreshToken.trim().isEmpty()) {

      throw new ParameterIsNullOrEmpty(
        ErrorCode.PARAMETER_IS_NULL_OR_EMPTY, REFRESH_TOKEN_NULL_OR_EMPTY_MSG);
    }

    refreshRepository
      .findByRefresh(refreshToken)
      .orElseThrow(() -> new RefreshTokenInvalidException(ErrorCode.REFRESH_TOKEN_INVALID));

    if (jwtUtil.isValid(refreshToken) != 0
      || !jwtUtil.REFRESH_TOKEN_PREFIX.equals(jwtUtil.getType(refreshToken))) {

      throw new RefreshTokenInvalidException(ErrorCode.REFRESH_TOKEN_INVALID);
    }
  }
}
