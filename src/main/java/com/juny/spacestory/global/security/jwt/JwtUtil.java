package com.juny.spacestory.global.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.juny.spacestory.global.exception.ErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
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
  public static final String ERROR_CODE = "code";
  public static final String ERROR_MSG = "msg";


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

  public Integer isValid(String token) {
    try {
      Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
    } catch (Exception e) {
      if (e instanceof ExpiredJwtException) {
        return 1;
      } else {
        e.printStackTrace();
        return 2;
      }
    }

    return -1;
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

  public void setErrorResponse(
    HttpServletResponse response, ErrorCode errorCode) throws IOException {
    response.setContentType(JwtUtil.CONTENT_TYPE);
    response.setStatus(errorCode.getStatus().value());

    Map<String, Object> data = new HashMap<>();
    data.put(JwtUtil.ERROR_CODE, errorCode.getCode());
    data.put(JwtUtil.ERROR_MSG, errorCode.getMsg());

    OutputStream out = response.getOutputStream();
    ObjectMapper mapper = new ObjectMapper();
    mapper.writeValue(out, data);
    out.flush();
  }
}
