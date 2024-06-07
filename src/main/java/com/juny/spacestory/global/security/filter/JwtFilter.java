package com.juny.spacestory.global.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.juny.spacestory.global.exception.ErrorCode;
import com.juny.spacestory.user.domain.Role;
import com.juny.spacestory.user.domain.User;
import com.juny.spacestory.global.security.jwt.JwtUtil;
import com.juny.spacestory.global.security.service.CustomUserDetails;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Slf4j
public class JwtFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;
  private final String AUTHORIZATION_HEADER = "Authorization";
  private final String AUTHORIZATION_PREFIX = "Bearer ";

  public JwtFilter(JwtUtil jwtUtil) {

    this.jwtUtil = jwtUtil;
  }


  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

    String authorization= request.getHeader(AUTHORIZATION_HEADER);

    if (authorization == null || !authorization.startsWith(AUTHORIZATION_PREFIX)) {
      filterChain.doFilter(request, response);
      return;
    }

    String token = authorization.split(" ")[1];
    if (!validateAccessToken(response, token)) {
      return;
    }

    storeSecurityContextHolder(request, response, filterChain, token);
  }

  private void storeSecurityContextHolder(HttpServletRequest request, HttpServletResponse response,
    FilterChain filterChain, String token) throws IOException, ServletException {
    String email = jwtUtil.getEmail(token);
    String role = jwtUtil.getRole(token);

    log.info("jwt token login, email: {}, role: {}", email, role);

    User user = new User(email, Role.valueOf(role));

    CustomUserDetails customUserDetails = new CustomUserDetails(user);

    Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

    SecurityContextHolder.getContext().setAuthentication(authToken);

    filterChain.doFilter(request, response);
  }

  private boolean validateAccessToken(HttpServletResponse response, String token) throws IOException {
    int errorType = jwtUtil.isValid(token);
    if (errorType == 1) {
        jwtUtil.setErrorResponse(response, ErrorCode.ACCESS_TOKEN_EXPIRED);
      return false;
    }
    if (errorType == 2 || JwtUtil.ACCESS_TOKEN_PREFIX.equals(jwtUtil.getType(token))) {
        jwtUtil.setErrorResponse(response, ErrorCode.ACCESS_TOKEN_INVALID);
      return false;
    }
    return true;
  }
}
