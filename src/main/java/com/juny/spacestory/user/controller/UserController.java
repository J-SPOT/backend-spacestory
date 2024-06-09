package com.juny.spacestory.user.controller;

import com.juny.spacestory.user.service.UserService;
import com.juny.spacestory.user.dto.ReqRegisterUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {

    this.userService = userService;
  }

  @Tag(name = "유저 인증 API", description = "회원 가입, 토큰 발행, 로그인")
  @Operation(summary = "회원 가입 요청 API")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "204", description = "로그인 성공"),
    @ApiResponse(responseCode = "P1", description = "400, 파라미터가 비어 있거나 NULL인 경우"),
    @ApiResponse(responseCode = "U4", description = "400, 이메일이 중복된 경우"),
    @ApiResponse(responseCode = "U5", description = "401, 패스워드가 일치하지 않는 경우"),
    @ApiResponse(responseCode = "U6", description = "400, 패스워드가 4자리 미만인 경우"),
  })

  @PostMapping("/api/v1/auth/register")
  public ResponseEntity<Void> register(@RequestBody ReqRegisterUser req) {
    userService.register(req);

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
