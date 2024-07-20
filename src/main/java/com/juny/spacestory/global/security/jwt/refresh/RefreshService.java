package com.juny.spacestory.global.security.jwt.refresh;

import com.juny.spacestory.global.exception.ErrorCode;
import com.juny.spacestory.global.exception.hierarchy.parameter.ParameterIsNullOrEmpty;
import com.juny.spacestory.global.exception.hierarchy.token.RefreshTokenInvalidException;
import com.juny.spacestory.global.security.jwt.JwtUtil;
import java.time.LocalDateTime;
import java.util.UUID;
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

    refreshRepository
        .findByRefresh(refreshToken)
        .orElseThrow(() -> new RefreshTokenInvalidException(ErrorCode.REFRESH_TOKEN_INVALID));

    if (jwtUtil.isValid(refreshToken) != 0
        || !jwtUtil.REFRESH_TOKEN_PREFIX.equals(jwtUtil.getType(refreshToken))) {

      throw new RefreshTokenInvalidException(ErrorCode.REFRESH_TOKEN_INVALID);
    }
  }

  private ResReissueTokens issueNewTokens(String refreshToken) {
    String id = jwtUtil.getId(refreshToken);
    String role = jwtUtil.getRole(refreshToken);

    String newAccessToken = jwtUtil.createJwt(jwtUtil.ACCESS_TOKEN_PREFIX, id, role);
    String newRefreshToken = jwtUtil.createJwt(jwtUtil.REFRESH_TOKEN_PREFIX, id, role);

    String newAccessTokenExpiration =
        jwtUtil.convertDateToLocalDateTime(jwtUtil.getExpiration(newAccessToken));
    String newRefreshTokenExpiration =
        jwtUtil.convertDateToLocalDateTime(jwtUtil.getExpiration(newRefreshToken));

    Refresh refresh = new Refresh(UUID.fromString(id), newRefreshToken, newRefreshTokenExpiration);
    refreshRepository.save(refresh);

    return new ResReissueTokens(
        newAccessToken, newRefreshToken, newAccessTokenExpiration, newRefreshTokenExpiration);
  }

  public void deleteExpiredTokens() {
    LocalDateTime now = LocalDateTime.now();

    refreshRepository.deleteExpiredTokens(now.toString());
  }

  public void logout(String refreshToken) {
    validateRefreshToken(refreshToken);

    refreshRepository.deleteByRefresh(refreshToken);
  }
}
