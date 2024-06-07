package com.juny.spacestory.global.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.juny.spacestory.global.exception.ErrorCode;
import com.juny.spacestory.global.security.jwt.refresh.Refresh;
import com.juny.spacestory.global.security.jwt.refresh.RefreshRepository;
import com.juny.spacestory.global.security.jwt.JwtUtil;
import com.juny.spacestory.global.security.service.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.juny.spacestory.global.security.jwt.JwtUtil.ACCESS_TOKEN_EXPIRAION;
import static com.juny.spacestory.global.security.jwt.JwtUtil.ACCESS_TOKEN_KEY;
import static com.juny.spacestory.global.security.jwt.JwtUtil.ACCESS_TOKEN_PREFIX;
import static com.juny.spacestory.global.security.jwt.JwtUtil.CHARACTER_ENCODING;
import static com.juny.spacestory.global.security.jwt.JwtUtil.CONTENT_TYPE;
import static com.juny.spacestory.global.security.jwt.JwtUtil.REFRESH_TOKEN_EXPIRAION;
import static com.juny.spacestory.global.security.jwt.JwtUtil.REFRESH_TOKEN_KEY;
import static com.juny.spacestory.global.security.jwt.JwtUtil.REFRESH_TOKEN_PREFIX;

@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;
  private final RefreshRepository refreshRepository;
  private final String PARAMETER_NULL_OR_EMPTY_MSG = "Parameter is null or empty.";
  private final String SET_USERNAME_PARAMETER = "email";
  private final String SET_LOGIN_ENDPOINT = "/api/v1/auth/login";
  private final String SET_LOGIN_ENDPOINT_METHOD = "POST";

  public LoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
    RefreshRepository refreshRepository) {

    this.authenticationManager = authenticationManager;
    this.jwtUtil = jwtUtil;
    this.refreshRepository = refreshRepository;
    setUsernameParameter(SET_USERNAME_PARAMETER);
    setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(SET_LOGIN_ENDPOINT, SET_LOGIN_ENDPOINT_METHOD));
  }

  @Override
  public Authentication attemptAuthentication(
      HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

    ReqLogin reqLogin = readByJson(request, response);

    log.info("attempt authentication, email: {}, password: {}", reqLogin.email(), reqLogin.password());

    UsernamePasswordAuthenticationToken authToken =
        new UsernamePasswordAuthenticationToken(reqLogin.email(), reqLogin.password(), null);

    return authenticationManager.authenticate(authToken);
  }

  private ReqLogin readByJson(HttpServletRequest request, HttpServletResponse response) {
    ReqLogin reqLogin = null;
    PrintWriter writer;

    try {
      reqLogin = new ObjectMapper().readValue(request.getInputStream(), ReqLogin.class);
    } catch (IOException e) {
      log.error(e.getMessage());
      throw new RuntimeException(e);
    }

    if (Objects.isNull(reqLogin)
      || reqLogin.email().trim().isEmpty() || reqLogin.password().trim().isEmpty()) {
      try {
        writer = response.getWriter();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      writer.print(PARAMETER_NULL_OR_EMPTY_MSG);
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return null;
    }

    return reqLogin;
  }

  @Override
  protected void successfulAuthentication(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain chain,
      Authentication authentication) throws IOException {

    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

    String email = customUserDetails.getUsername();

    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
    Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
    GrantedAuthority auth = iterator.next();

    String role = auth.getAuthority();

    String accessToken = jwtUtil.createJwt(ACCESS_TOKEN_PREFIX, email, role);
    String refreshToken = jwtUtil.createJwt(REFRESH_TOKEN_PREFIX, email, role);

    String accessTokenExpired = jwtUtil.convertDateToLocalDateTime(jwtUtil.getExpiration(accessToken));
    String refreshTokenExpired = jwtUtil.convertDateToLocalDateTime(jwtUtil.getExpiration(refreshToken));

    refreshRepository.save(new Refresh(email, refreshToken, refreshTokenExpired));

    response.setContentType(CONTENT_TYPE);
    response.setCharacterEncoding(CHARACTER_ENCODING);

    Map<String, String> responseBody = new HashMap<>();
    responseBody.put(ACCESS_TOKEN_KEY, accessToken);
    responseBody.put(REFRESH_TOKEN_KEY, refreshToken);
    responseBody.put(ACCESS_TOKEN_EXPIRAION, accessTokenExpired);
    responseBody.put(REFRESH_TOKEN_EXPIRAION, refreshTokenExpired);

    PrintWriter writer = response.getWriter();
    writer.write(new ObjectMapper().writeValueAsString(responseBody));
    writer.flush();
  }

  @Override
  protected void unsuccessfulAuthentication(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException failed)
    throws IOException {

    jwtUtil.setErrorResponse(response, ErrorCode.USER_NOT_MATCH_PASSWORD);
  }
}
