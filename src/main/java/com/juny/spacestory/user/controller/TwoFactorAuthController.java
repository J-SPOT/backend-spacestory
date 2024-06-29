package com.juny.spacestory.user.controller;

import com.juny.spacestory.global.exception.ErrorResponse;
import com.juny.spacestory.user.dto.ResTokens;
import com.juny.spacestory.user.service.TwoFactorAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TwoFactorAuthController {

  private final TwoFactorAuthService twoFactorAuthService;

  @Tag(name = "유저 인증 API", description = "회원 가입, 토큰 발행, 로그인, 로그아웃")
  @Operation(summary = "새로운 장소에서 로그인한 경우 이메일 2차 인증 및 액세스 토큰과 리프레시 토큰 발급 API",
    description = "{도메인}/login/2fa/login 페이지에서 이메일 인증 코드를 입력 후 해당 API를 호출하여 토큰을 발급받아야 합니다."
  )
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "로그인한 IP 추가 및 토큰 발급"),
      @ApiResponse(
        responseCode = "P1",
        description = "400, 파라미터가 비어 있거나 널인 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(
        responseCode = "U7",
        description = "400, 이메일이 올바르지 않은 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(
        responseCode = "EM1",
        description = "401, 이메일 인증 코드가 유효하지 않은 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(
        responseCode = "EM2",
        description = "400, 이메일 인증 코드가 만료된 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(
        responseCode = "RT1",
        description = "401, 리프레시 토큰이 유효하지 않은 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
  @PostMapping("/api/v1/auth/login/email-verification/verify")
  public ResponseEntity<ResTokens> verifyEmailCodeAndAddIPAddress(HttpServletRequest request,  HttpServletResponse response,  @CookieValue String refreshToken, @RequestBody EmailCode reqCode) {

    System.out.println("refreshToken = " + refreshToken);
    ResTokens resTokens = twoFactorAuthService.verifyEmailCodeAndIssueTokens(refreshToken, reqCode.code(),
      request.getRemoteAddr(), false);

    response.addHeader("Set-Cookie", deleteCookie("refresh", null, 0L).toString());

    return new ResponseEntity<>(resTokens, HttpStatus.OK);
  }

  private record EmailCode(String code) {}

  @Tag(name = "유저 인증 API", description = "회원 가입, 토큰 발행, 로그인, 로그아웃")
  @Operation(summary = "Totp 2차 인증 활성화한 경우 totp 코드 검증 및 액세스 토큰과 리프레시 토큰 발급 API",
    description = "{도메인}/login/2fa/totp 페이지에서 totp 인증 코드를 입력 후 해당 API를 호출하여 토큰을 발급받아야 합니다."
  )
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "로그인한 IP 추가 및 토큰 발급"),
      @ApiResponse(
        responseCode = "P1",
        description = "400, 파라미터가 비어 있거나 널인 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(
        responseCode = "U7",
        description = "400, 이메일이 올바르지 않은 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(
        responseCode = "RT1",
        description = "401, 리프레시 토큰이 유효하지 않은 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(
        responseCode = "TO1",
        description = "400, TOTP 활성화되지 않은 유저인 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(
        responseCode = "TO2",
        description = "400, TOTP 코드가 유효하지 않은 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
  @PostMapping("/api/v1/auth/login/totp-verification/verify")
  public ResponseEntity<ResTokens> verifyTotpCodeAndAddIpAddress(HttpServletRequest request,  HttpServletResponse response,  @CookieValue String refreshToken, @RequestBody TotpCode code) {

    System.out.println("TwoFactorAuthController.verifyTotpCodeAndAddIpAddress");

    ResTokens resTokens = twoFactorAuthService.verifyTotpCodeAndIssueTokens(refreshToken, code.code(),
      request.getRemoteAddr());

    response.addHeader("Set-Cookie", deleteCookie("refresh", null, 0L).toString());

    return new ResponseEntity<>(resTokens, HttpStatus.OK);
  }

  private record TotpCode(Integer code) {}

  private ResponseCookie deleteCookie(String key, String value, Long expiration) {

    return ResponseCookie.from(key, value)
      .path("/")
      .maxAge(expiration)
      .httpOnly(true)
      .secure(true)
      .sameSite("None")
      .build();
  }
}
