package com.juny.spacestory.global.security.jwt.refresh;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RefreshController {

  private final RefreshService refreshService;

  public RefreshController(RefreshService refreshService) {
    this.refreshService = refreshService;
  }

  @Tag(name = "유저 인증 API", description = "회원 가입, 토큰 발행, 로그인, 로그아웃")
  @Operation(
      summary = "액세스 토큰과 리프레시 토큰 재발행하는 API",
      description =
          "API를 요청할 때 액세스 토큰이 만료되었다는 응답 코드인 \"AT1\"을 받으면 해당 엔드포인트에서 유효한 Refresh Token으로 액세스 토큰과 리프레시 토큰을 갱신 받아야 합니다.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "로그인 성공"),
        @ApiResponse(responseCode = "P1", description = "400, 파라미터가 비어 있거나 널인 경우"),
        @ApiResponse(responseCode = "RT1", description = "401, 리프레시 토큰이 유효하지 않은 경우"),
      })
  @PostMapping("/api/v1/auth/tokens")
  public ResponseEntity<ResReissueTokens> reissue(@RequestBody ReqReissueTokens req) {

    ResReissueTokens resReissueTokens = refreshService.reissue(req.refreshToken());

    return new ResponseEntity<>(resReissueTokens, HttpStatus.OK);
  }

  @Tag(name = "유저 인증 API", description = "회원 가입, 토큰 발행, 로그인, 로그아웃")
  @Operation(
    summary = "로그아웃 요청 API",
    description = "사용자가 로그아웃을 하면 해당 리프레시 토큰을 재사용할 수 없게 해당 앤드포인트를 호출해야 합니다.")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "로그인 성공"),
      @ApiResponse(responseCode = "P1", description = "400, 파라미터가 비어 있거나 널인 경우"),
      @ApiResponse(responseCode = "RT1", description = "401, 리프레시 토큰이 유효하지 않은 경우"),
    })
  @PostMapping("/api/v1/auth/logout")
  public ResponseEntity<Void> logout(@RequestBody ReqReissueTokens req) {

    refreshService.logout(req.refreshToken());

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @GetMapping("/api/v1/auth/tokens-by-cookie")
  public ResponseEntity<ResReissueTokens> reissueByCookie(@CookieValue(value = "refresh", required = true) String refreshToken, HttpServletResponse response) {

    ResReissueTokens resReissueTokens = refreshService.reissue(refreshToken);

    response.addCookie(deleteCookie("refresh", null, 0L));

    return new ResponseEntity<>(resReissueTokens, HttpStatus.OK);
  }

  private Cookie deleteCookie(String refresh, String refreshToken, Long expiration) {
    Cookie cookie = new Cookie(refresh, refreshToken);
    cookie.setMaxAge(expiration.intValue());
    cookie.setPath("/");
    cookie.setHttpOnly(true);
//    cookie.setSecure(true);
//    cookie.setDomain("spacestory.duckdns.org");

    return cookie;
  }

}
