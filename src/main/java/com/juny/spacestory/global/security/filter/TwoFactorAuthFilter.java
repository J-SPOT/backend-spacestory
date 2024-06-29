package com.juny.spacestory.global.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.juny.spacestory.global.security.jwt.JwtUtil;
import com.juny.spacestory.global.security.jwt.refresh.Refresh;
import com.juny.spacestory.global.security.jwt.refresh.RefreshRepository;
import com.juny.spacestory.global.security.service.CustomUserDetails;
import com.juny.spacestory.user.dto.ResSecurityContextHolder;
import com.juny.spacestory.user.service.EmailVerificationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class TwoFactorAuthFilter extends OncePerRequestFilter {

  private final EmailVerificationService emailVerificationService;
  private final JwtUtil jwtUtil;
  private final RefreshRepository refreshRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
    FilterChain filterChain) throws ServletException, IOException {

    String requestURI = request.getRequestURI();

    if (!requestURI.equals("/api/v1/auth/login")) {
      filterChain.doFilter(request, response);
      return;
    }

    ResSecurityContextHolder sh = getSecurityContextHolder();

    if (sh.totpEnabled()) {

      setRefreshTokenByCookie(response, sh);

//      response.sendRedirect("https://spacestory.duckdns.org/login/2fa/totp");
      response.sendRedirect("http://localhost:5173/login/2fa/totp");
      return;
    }

    if (!sh.ipAddresses().contains(request.getRemoteAddr())) {

      emailVerificationService.sendCode(sh.email(), false);

      setRefreshTokenByCookie(response, sh);

//      response.sendRedirect("https://spacestory.duckdns.org/login/2fa/email");
//      response.sendRedirect("http://localhost:5173/login/2fa/email");

      response.setContentType(jwtUtil.CONTENT_TYPE);
      response.setCharacterEncoding(jwtUtil.CHARACTER_ENCODING);

      Map<String, String> responseBody = new HashMap<>();
      responseBody.put("redirectUrl", "http://localhost:5173/login/2fa/email");

      PrintWriter writer = response.getWriter();
      writer.write(new ObjectMapper().writeValueAsString(responseBody));
      writer.flush();
    }
  }

  private void setRefreshTokenByCookie(HttpServletResponse response, ResSecurityContextHolder sh) {

    System.out.println("TwoFactorAuthFilter.setRefreshTokenByCookie");

    String refreshToken = jwtUtil.createJwt(jwtUtil.REFRESH_TOKEN_PREFIX, sh.id(), sh.role());

    String refreshTokenExpired =
      jwtUtil.convertDateToLocalDateTime(jwtUtil.getExpiration(refreshToken));

    refreshRepository.save(new Refresh(UUID.fromString(sh.id()), refreshToken, refreshTokenExpired));

    response.addHeader(
      "Set-Cookie",
      createCookie(jwtUtil.REFRESH_TOKEN_PREFIX, refreshToken, jwtUtil.REFRESH_TOKEN_EXPIRED)
        .toString());
  }

  private ResSecurityContextHolder getSecurityContextHolder() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
    Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
    GrantedAuthority auth = iterator.next();

    String id = customUserDetails.getId();
    String email = customUserDetails.getEmail();
    boolean totpEnabled = customUserDetails.isTotpEnabled();
    List<String> ipAddresses = customUserDetails.getIpAddresses();
    String role = auth.getAuthority();

    return new ResSecurityContextHolder(id, email, totpEnabled, ipAddresses, role);
  }

  private ResponseCookie createCookie(String key, String value, Long expiration) {

    return ResponseCookie.from(key, value)
      .path("/")
      .maxAge(expiration)
      .httpOnly(true)
      .secure(true)
      .sameSite("None")
      .build();
  }
}
