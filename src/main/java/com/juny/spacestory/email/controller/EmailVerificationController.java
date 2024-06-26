package com.juny.spacestory.email.controller;

import com.juny.spacestory.email.dto.ReqCode;
import com.juny.spacestory.email.dto.ReqEmail;
import com.juny.spacestory.email.service.EmailVerificationService;
import com.juny.spacestory.global.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EmailVerificationController {

  private final EmailVerificationService emailVerificationService;

  @Tag(name = "유저 인증 API", description = "회원 가입, 토큰 발행, 로그인, 로그아웃")
  @Operation(
    summary = "이메일 인증 시 인증 코드 발급 API",
    description = "인증 코드는 3분간 유효합니다.")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "204", description = "사용자가 지정한 메일로 인증 코드 발송 성공"),
      @ApiResponse(
        responseCode = "P1",
        description = "400, 파라미터가 비어 있거나 널인 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(
        responseCode = "U7",
        description = "400, 이메일 형식이 올바르지 않은 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
  @PostMapping("/api/v1/verification-codes")
  public ResponseEntity<Void> sendVerificationEmail(@RequestBody ReqEmail req) {

    emailVerificationService.sendCode(req.email());

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Tag(name = "유저 인증 API", description = "회원 가입, 토큰 발행, 로그인, 로그아웃")
  @Operation(summary = "이메일 인증 시 인증 코드 검증 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "204", description = "사용자가 지정한 메일로 인증 코드 발송 성공"),
      @ApiResponse(
        responseCode = "P1",
        description = "400, 파라미터가 비어 있거나 널인 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(
        responseCode = "U7",
        description = "400, 이메일 형식이 올바르지 않은 경우",
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
    })
  @PostMapping("/api/v1/verification-codes/verify")
  public ResponseEntity<Void> verifyCode(@RequestBody ReqCode req) {

    emailVerificationService.verifyCode(req.email(), req.code());

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
