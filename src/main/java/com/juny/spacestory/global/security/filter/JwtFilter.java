package com.juny.spacestory.global.security.filter;

import com.juny.spacestory.user.domain.Role;
import com.juny.spacestory.user.domain.User;
import com.juny.spacestory.global.security.jwt.JwtUtil;
import com.juny.spacestory.global.security.service.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Slf4j
public class JwtFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;

  public JwtFilter(JwtUtil jwtUtil) {

    this.jwtUtil = jwtUtil;
  }


  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

    String authorization= request.getHeader("Authorization");

    if (authorization == null || !authorization.startsWith("Bearer ")) {

      filterChain.doFilter(request, response);
      return;
    }

    String token = authorization.split(" ")[1];

    if (!jwtUtil.isValid(token)) {

      filterChain.doFilter(request, response);
      return;
    }

    String email = jwtUtil.getEmail(token);
    String role = jwtUtil.getRole(token);

    log.info("jwt token login, email: {}, role: {}", email, role);

    User user = new User(email, Role.valueOf(role));

    CustomUserDetails customUserDetails = new CustomUserDetails(user);

    Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

    SecurityContextHolder.getContext().setAuthentication(authToken);

    filterChain.doFilter(request, response);
  }
}
