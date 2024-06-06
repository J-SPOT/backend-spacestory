package com.juny.spacestory.global.security.jwt;

import io.jsonwebtoken.Jwts;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

  public static final String CONTENT_TYPE = "application/json";
  public static final String CHARACTER_ENCODING = "UTF-8";
  public static final String ACCESS_TOKEN_PREFIX = "access";
  public static final String REFRESH_TOKEN_PREFIX = "refresh";
  public static final String ACCESS_TOKEN_KEY = "accessToken";
  public static final String REFRESH_TOKEN_KEY = "refreshToken";
  public static final String ACCESS_TOKEN_EXPIRAION = "accessTokenExpired";
  public static final String REFRESH_TOKEN_EXPIRAION = "refreshTokenExpired";
  public static final String JWT_CLAIM_TYPE = "type";
  public static final String JWT_CLAIM_EMAIL = "email";
  public static final String JWT_CLAIM_ROLE = "role";
  public static final Long ACCESS_TOKEN_EXPIRED = 60 * 5 * 1000L; // 5분
  public static final Long REFRESH_TOKEN_EXPIRED = 60 * 60 * 24 * 1000L; // 1일

  private final SecretKey secretKey;

  public JwtUtil(@Value("${jwt.secretKey}") String secret) {

    secretKey =
        new SecretKeySpec(
            secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
  }

  public String getEmail(String token) {

    return Jwts.parser()
        .verifyWith(secretKey)
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .get(JWT_CLAIM_EMAIL, String.class);
  }

  public String getRole(String token) {

    return Jwts.parser()
        .verifyWith(secretKey)
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .get(JWT_CLAIM_ROLE, String.class);
  }

  public Boolean isValid(String token) {

    try {

      Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
    } catch (Exception e) {

      log.error("token is invalid: {}", e.getMessage());
      return false;
    }

    return true;
  }

  public String getType(String token) {

    return Jwts.parser()
        .verifyWith(secretKey)
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .get(JWT_CLAIM_TYPE, String.class);
  }

  public Date getExpiration(String token) {

    return Jwts.parser()
        .verifyWith(secretKey)
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .getExpiration();
  }

  public String createJwt(String type, String email, String role) {

    Long expiredMs = ACCESS_TOKEN_EXPIRED;

    if (REFRESH_TOKEN_PREFIX.equals(type)) {

      expiredMs = REFRESH_TOKEN_EXPIRED;
    }

    return Jwts.builder()
        .claim(JWT_CLAIM_TYPE, type)
        .claim(JWT_CLAIM_EMAIL, email)
        .claim(JWT_CLAIM_ROLE, role)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + expiredMs))
        .signWith(secretKey)
        .compact();
  }

  public String convertDateToLocalDateTime(Date date) {
    Instant instant = date.toInstant();
    LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    return localDateTime.toString();
  }
}
