package com.juny.spacestory.global.security.jwt.refresh;

import com.juny.spacestory.global.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
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
        @ApiResponse(
            responseCode = "200",
            description = "토큰 재발행 성공",
            content = @Content(schema = @Schema(implementation = ResReissueTokens.class))),
        @ApiResponse(
            responseCode = "P1",
            description = "400, 파라미터가 비어 있거나 널인 경우",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "RT1",
            description = "401, 리프레시 토큰이 유효하지 않은 경우",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
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
        @ApiResponse(responseCode = "204", description = "로그아웃 성공"),
        @ApiResponse(
            responseCode = "P1",
            description = "400, 파라미터가 비어 있거나 널인 경우",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "RT1",
            description = "401, 리프레시 토큰이 유효하지 않은 경우",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      })
  @PostMapping("/api/v1/auth/logout")
  public ResponseEntity<Void> logout(@RequestBody ReqReissueTokens req) {

    refreshService.logout(req.refreshToken());

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Tag(name = "유저 인증 API", description = "회원 가입, 토큰 발행, 로그인, 로그아웃")
  @Operation(
      summary = "OAuth2.0 로그인 성공 후 핸들러에 의해 호출되는 API",
      description =
          "{도메인}/social_login_handler?social_login=success 리다이렉트하여 핸들러를 호출할 수 있는 URL을 제공합니다.<br>서버는 쿠키에 임시 RefreshToken을 발급한 상태입니다. 해당 API를 호출하여 쿠키를 검증하고, 유효하다면 응답메시지에 액세스 토큰과 리프레시 토큰을 새로 발급합니다. 기존 쿠키는 삭제합니다.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "토큰 재발행 성공",
            content = @Content(schema = @Schema(implementation = ResReissueTokens.class))),
        @ApiResponse(
            responseCode = "P1",
            description = "400, 파라미터가 비어 있거나 널인 경우",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "RT1",
            description = "401, 리프레시 토큰이 유효하지 않은 경우",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      })
  @GetMapping("/api/v1/auth/tokens-by-cookie")
  public ResponseEntity<ResReissueTokens> reissueByCookie(
      @CookieValue String refreshToken, HttpServletResponse response) {

    ResReissueTokens resReissueTokens = refreshService.reissue(refreshToken);

    response.addHeader("Set-Cookie", deleteCookie("refresh", null, 0L).toString());

    return new ResponseEntity<>(resReissueTokens, HttpStatus.OK);
  }

  private ResponseCookie deleteCookie(String refresh, String refreshToken, Long expiration) {

    return ResponseCookie.from(refresh, refreshToken)
        .path("/")
        .maxAge(expiration)
        .httpOnly(true)
        .secure(true)
        .sameSite("None")
        .build();
  }
}
