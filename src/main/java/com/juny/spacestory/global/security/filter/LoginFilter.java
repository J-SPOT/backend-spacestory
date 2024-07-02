package com.juny.spacestory.global.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.juny.spacestory.global.exception.ErrorCode;
import com.juny.spacestory.global.security.jwt.refresh.Refresh;
import com.juny.spacestory.global.security.jwt.refresh.RefreshRepository;
import com.juny.spacestory.global.security.jwt.JwtUtil;
import com.juny.spacestory.global.security.service.CustomUserDetails;
import com.juny.spacestory.login.LoginAttemptService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;
  private final RefreshRepository refreshRepository;
  private final LoginAttemptService loginAttemptService;

  private final String SET_USERNAME_PARAMETER = "email";
  private final String SET_LOGIN_ENDPOINT = "/api/v1/auth/login";
  private final String SET_LOGIN_ENDPOINT_METHOD = "POST";

  public LoginFilter(
      AuthenticationManager authenticationManager,
      JwtUtil jwtUtil,
      RefreshRepository refreshRepository, LoginAttemptService loginAttemptService) {

    this.authenticationManager = authenticationManager;
    this.jwtUtil = jwtUtil;
    this.refreshRepository = refreshRepository;
    this.loginAttemptService = loginAttemptService;
    setUsernameParameter(SET_USERNAME_PARAMETER);
    setRequiresAuthenticationRequestMatcher(
        new AntPathRequestMatcher(SET_LOGIN_ENDPOINT, SET_LOGIN_ENDPOINT_METHOD));
  }

  @Override
  public Authentication attemptAuthentication(
      HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

    log.info("attemptAuthentication");
    ReqLogin reqLogin = readByJson(request, response);
    if (reqLogin == null) {

      throw new AuthenticationServiceException(ErrorCode.PARAMETER_IS_NULL_OR_EMPTY.getMsg());
    }

    UsernamePasswordAuthenticationToken authToken =
        new UsernamePasswordAuthenticationToken(reqLogin.email(), reqLogin.password(), null);

    log.info("authenticate");
    return authenticationManager.authenticate(authToken);
  }

  private ReqLogin readByJson(HttpServletRequest request, HttpServletResponse response) {
    ReqLogin reqLogin = null;

    try {
      reqLogin = new ObjectMapper().readValue(request.getInputStream(), ReqLogin.class);
    } catch (Exception e) {
      log.error(e.getMessage());
      return null;
    }

    if (Objects.isNull(reqLogin)
        || Objects.isNull(reqLogin.email())
        || Objects.isNull(reqLogin.password())
        || reqLogin.email().trim().isEmpty()
        || reqLogin.password().trim().isEmpty()) {

      return null;
    }
    request.setAttribute("email", reqLogin.email());

    return reqLogin;
  }

  @Override
  protected void successfulAuthentication(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain chain,
      Authentication authentication)
    throws IOException, ServletException {

    log.info("successfulAuthentication");
    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
    Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
    GrantedAuthority auth = iterator.next();

    String id = customUserDetails.getId();
    boolean totpEnabled = customUserDetails.isTotpEnabled();
    String role = auth.getAuthority();
    List<String> ipAddresses = customUserDetails.getIpAddresses();

    SecurityContextHolder.getContext().setAuthentication(authentication);

    if (ipAddresses.contains(request.getRemoteAddr()) && !totpEnabled) {
      issueTokens(response, id, role);
      return;
    }

    chain.doFilter(request, response);
  }

  private void issueTokens(HttpServletResponse response, String id, String role) throws IOException {
    String accessToken = jwtUtil.createJwt(jwtUtil.ACCESS_TOKEN_PREFIX, id, role);
    String refreshToken = jwtUtil.createJwt(jwtUtil.REFRESH_TOKEN_PREFIX, id, role);

    String accessTokenExpired =
        jwtUtil.convertDateToLocalDateTime(jwtUtil.getExpiration(accessToken));
    String refreshTokenExpired =
        jwtUtil.convertDateToLocalDateTime(jwtUtil.getExpiration(refreshToken));

    refreshRepository.save(new Refresh(UUID.fromString(id), refreshToken, refreshTokenExpired));

    response.setContentType(jwtUtil.CONTENT_TYPE);
    response.setCharacterEncoding(jwtUtil.CHARACTER_ENCODING);

    Map<String, String> responseBody = new HashMap<>();
    responseBody.put(jwtUtil.ACCESS_TOKEN_KEY, accessToken);
    responseBody.put(jwtUtil.REFRESH_TOKEN_KEY, refreshToken);
    responseBody.put(jwtUtil.ACCESS_TOKEN_EXPIRAION, accessTokenExpired);
    responseBody.put(jwtUtil.REFRESH_TOKEN_EXPIRAION, refreshTokenExpired);

    loginAttemptService.loginSuccess(id);

    PrintWriter writer = response.getWriter();
    writer.write(new ObjectMapper().writeValueAsString(responseBody));
    writer.flush();
  }

  @Override
  protected void unsuccessfulAuthentication(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException failed)
      throws IOException {

    jwtUtil.setErrorResponse(response, ErrorCode.UNAUTHORIZED, failed.getMessage());

    loginAttemptService.loginFailed(request.getAttribute("email").toString());
  }
}
