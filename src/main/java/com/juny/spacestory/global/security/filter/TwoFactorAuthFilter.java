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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class TwoFactorAuthFilter extends OncePerRequestFilter {

  private final EmailVerificationService emailVerificationService;
  private final JwtUtil jwtUtil;
  private final RefreshRepository refreshRepository;
  private final String LOGIN_ENDPOINT = "/api/v1/auth/login";
  private final String TOTP_REDIRECT_URL;
  private final String EMAIL_REDIRECT_URL;
  private final String REDIRECT_URL_KEY = "redirectUrl";
  private final String EMAIL_KEY = "email";
  private final String SET_COOKIE_KEY = "Set-Cookie";

  public TwoFactorAuthFilter(EmailVerificationService emailVerificationService, JwtUtil jwtUtil,
    RefreshRepository refreshRepository, String totpRedirectUrl, String EMAIL_REDIRECT_URL) {
    this.emailVerificationService = emailVerificationService;
    this.jwtUtil = jwtUtil;
    this.refreshRepository = refreshRepository;
    this.TOTP_REDIRECT_URL = totpRedirectUrl;
    this.EMAIL_REDIRECT_URL = EMAIL_REDIRECT_URL;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
    FilterChain filterChain) throws ServletException, IOException {

    String requestURI = request.getRequestURI();

    log.info("Two-factor auth filter TOTP_REDIRECT_EMAIL: {}", TOTP_REDIRECT_URL);
    log.info("Two-factor auth filter EMAIL_REDIRECT_EMAIL: {}", EMAIL_REDIRECT_URL);

    if (!requestURI.equals(LOGIN_ENDPOINT)) {
      filterChain.doFilter(request, response);
      return;
    }

    ResSecurityContextHolder sh = getSecurityContextHolder();

    if (sh.totpEnabled()) {

      setRefreshTokenByCookie(response, sh);

      setCookieAndRedirectUrlAndEmail(response, TOTP_REDIRECT_URL, sh.email());
      return;
    }

    if (!sh.ipAddresses().contains(request.getRemoteAddr())) {

      emailVerificationService.sendCode(sh.email(), false);

      setRefreshTokenByCookie(response, sh);

      setCookieAndRedirectUrl(response, EMAIL_REDIRECT_URL);
    }
  }

  private void setCookieAndRedirectUrlAndEmail(HttpServletResponse response, String url,
    String email) throws IOException {
    response.setContentType(jwtUtil.CONTENT_TYPE);
    response.setCharacterEncoding(jwtUtil.CHARACTER_ENCODING);

    Map<String, String> responseBody = new HashMap<>();
    responseBody.put(REDIRECT_URL_KEY, url);
    responseBody.put(EMAIL_KEY, email);

    PrintWriter writer = response.getWriter();
    writer.write(new ObjectMapper().writeValueAsString(responseBody));
    writer.flush();
  }

  private void setCookieAndRedirectUrl(HttpServletResponse response, String url)
    throws IOException {
    response.setContentType(jwtUtil.CONTENT_TYPE);
    response.setCharacterEncoding(jwtUtil.CHARACTER_ENCODING);

    Map<String, String> responseBody = new HashMap<>();
    responseBody.put(REDIRECT_URL_KEY, url);

    PrintWriter writer = response.getWriter();
    writer.write(new ObjectMapper().writeValueAsString(responseBody));
    writer.flush();
  }

  private void setRefreshTokenByCookie(HttpServletResponse response, ResSecurityContextHolder sh) {

    String refreshToken = jwtUtil.createJwt(jwtUtil.REFRESH_TOKEN_PREFIX, sh.id(), sh.role());

    String refreshTokenExpired =
      jwtUtil.convertDateToLocalDateTime(jwtUtil.getExpiration(refreshToken));

    refreshRepository.save(new Refresh(UUID.fromString(sh.id()), refreshToken, refreshTokenExpired));

    response.addHeader(
      SET_COOKIE_KEY,
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
    boolean totpEnabled = customUserDetails.getIsTotpEnabled();
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
