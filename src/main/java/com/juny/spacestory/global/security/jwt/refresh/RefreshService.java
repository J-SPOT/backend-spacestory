package com.juny.spacestory.global.security.jwt.refresh;

import com.juny.spacestory.global.exception.ErrorCode;
import com.juny.spacestory.global.exception.hierarchy.parameter.ParameterIsNullOrEmpty;
import com.juny.spacestory.global.exception.hierarchy.token.RefreshTokenInvalidException;
import com.juny.spacestory.global.security.jwt.JwtUtil;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
public class RefreshService {

  private final JwtUtil jwtUtil;
  private final RefreshRepository refreshRepository;
  private final String REFRESH_TOKEN_NULL_OR_EMPTY_MSG = "Refresh token is null or empty.";

  public RefreshService(JwtUtil jwtUtil, RefreshRepository refreshRepository) {
    this.jwtUtil = jwtUtil;
    this.refreshRepository = refreshRepository;
  }

  // 1. 토큰 서명 유효성을 검증한다.
  // 2. 토큰이 저장소에 있는지 검증한다.
  // 3. 기존 리프레시 토큰을 제거하고, 새로 발급한다.
  public ResReissueTokens reissue(String refreshToken) {

    validateRefreshToken(refreshToken);

    refreshRepository.deleteByRefresh(refreshToken);

    return issueNewTokens(refreshToken);
  }

  private void validateRefreshToken(String refreshToken) {

    if (refreshToken == null || refreshToken.trim().isEmpty()) {

      throw new ParameterIsNullOrEmpty(
          ErrorCode.PARAMETER_IS_NULL_OR_EMPTY, REFRESH_TOKEN_NULL_OR_EMPTY_MSG);
    }

    if (jwtUtil.isValid(refreshToken) < 0
        || JwtUtil.REFRESH_TOKEN_PREFIX.equals(jwtUtil.getType(refreshToken))) {

      throw new RefreshTokenInvalidException(ErrorCode.REFRESH_TOKEN_INVALID);
    }
  }

  private ResReissueTokens issueNewTokens(String refreshToken) {
    String email = jwtUtil.getEmail(refreshToken);
    String role = jwtUtil.getRole(refreshToken);

    String newAccessToken = jwtUtil.createJwt(JwtUtil.ACCESS_TOKEN_PREFIX, email, role);
    String newRefreshToken = jwtUtil.createJwt(JwtUtil.REFRESH_TOKEN_PREFIX, email, role);

    String newAccessTokenExpiration =
        jwtUtil.convertDateToLocalDateTime(jwtUtil.getExpiration(newAccessToken));
    String newRefreshTokenExpiration =
        jwtUtil.convertDateToLocalDateTime(jwtUtil.getExpiration(newRefreshToken));

    Refresh refresh = new Refresh(email, newRefreshToken, newRefreshTokenExpiration);
    refreshRepository.save(refresh);

    return new ResReissueTokens(
        newAccessToken, newRefreshToken, newAccessTokenExpiration, newRefreshTokenExpiration);
  }

  public void deleteExpiredTokens() {
    LocalDateTime now = LocalDateTime.now();

    refreshRepository.deleteExpiredTokens(now.toString());
  }
}
