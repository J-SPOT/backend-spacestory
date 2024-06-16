package com.juny.spacestory.global.security.oauth2;

import com.juny.spacestory.global.security.jwt.JwtUtil;
import com.juny.spacestory.global.security.jwt.refresh.Refresh;
import com.juny.spacestory.global.security.jwt.refresh.RefreshRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final JwtUtil jwtUtil;
  private final RefreshRepository refreshRepository;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
    Authentication authentication) throws IOException, ServletException {

    log.info("onAuthenticationSuccess");

    CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

    String id = customUserDetails.getId();

    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
    Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
    GrantedAuthority auth = iterator.next();

    String role = auth.getAuthority();

    System.out.println("id = " + id);
    System.out.println("role = " + role);

    String refreshToken = jwtUtil.createJwt(jwtUtil.REFRESH_TOKEN_PREFIX, id, role);

    String refreshTokenExpired =
      jwtUtil.convertDateToLocalDateTime(jwtUtil.getExpiration(refreshToken));

    refreshRepository.save(new Refresh(UUID.fromString(id), refreshToken, refreshTokenExpired));

    response.addCookie(createCookie("refresh", refreshToken, jwtUtil.REFRESH_TOKEN_EXPIRED));
    response.sendRedirect("http://localhost:5173/social_login_handler?social_login=success");
  }

  private Cookie createCookie(String refresh, String refreshToken, Long expiration) {
    Cookie cookie = new Cookie(refresh, refreshToken);
    cookie.setMaxAge(expiration.intValue());
    cookie.setPath("/");
    cookie.setHttpOnly(true);
//    cookie.setSecure(true);
//    cookie.setDomain("spacestory.duckdns.org");

    return cookie;
  }
}
