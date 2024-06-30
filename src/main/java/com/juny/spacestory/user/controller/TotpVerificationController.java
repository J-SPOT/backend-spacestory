package com.juny.spacestory.user.controller;

import com.google.zxing.WriterException;
import com.juny.spacestory.global.exception.ErrorResponse;
import com.juny.spacestory.user.dto.ReqTotpCode;
import com.juny.spacestory.user.service.TotpVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TotpVerificationController {

  private final TotpVerificationService totpVerificationService;

  @Tag(name = "유저 인증 API", description = "회원 가입, 토큰 발행, 로그인, 로그아웃")
  @Operation(
    summary = "Totp 인증 활성화할 수 있게 QR코드 생성하는 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "204", description = "QR코드 생성 성공"),
      @ApiResponse(
        responseCode = "P1",
        description = "400, 파라미터가 비어 있거나 널인 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(
        responseCode = "U7",
        description = "400, 이메일이 올바르지 않은 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
  @PostMapping("/api/v1/auth/totp-verification/qr")
  public ResponseEntity<String> createTotpQrCode(@RequestBody Email email)
    throws IOException, WriterException {

    String totpQrCode = totpVerificationService.createTotpQrCode(email.email());

    return new ResponseEntity<>(totpQrCode, HttpStatus.OK);
  }
  private record Email(String email) {}

  @Tag(name = "유저 인증 API", description = "회원 가입, 토큰 발행, 로그인, 로그아웃")
  @Operation(
    summary = "Totp 코드 검증 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "204", description = "Totp 코드 검증 성공"),
      @ApiResponse(
        responseCode = "P1",
        description = "400, 파라미터가 비어 있거나 널인 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(
        responseCode = "TO1",
        description = "400, Totp 활성화되지 않은 유저인 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(
        responseCode = "TO2",
        description = "400, Totp 코드가 유효하지 않은 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
  @PostMapping("/api/v1/auth/totp-verification/verify")
  public ResponseEntity<Void> verifyTotpCode(@RequestBody ReqTotpCode req) {

    totpVerificationService.verifyTotpCode(req.email(), req.code());

    return new ResponseEntity<>(HttpStatus.OK);
  }
}
