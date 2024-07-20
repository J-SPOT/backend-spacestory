package com.juny.spacestory.user.controller;

import com.juny.spacestory.global.exception.ErrorResponse;
import com.juny.spacestory.global.security.service.CustomUserDetails;
import com.juny.spacestory.user.dto.ReqModifyPassword;
import com.juny.spacestory.user.dto.ReqModifyProfile;
import com.juny.spacestory.user.dto.ResLookUpUser;
import com.juny.spacestory.user.dto.ResLookUpUsers;
import com.juny.spacestory.user.dto.ResModifyUser;
import com.juny.spacestory.user.service.UserService;
import com.juny.spacestory.user.dto.ReqRegisterUser;
import com.juny.spacestory.util.IpUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

  private final UserService userService;

  @Tag(name = "유저 인증 API", description = "회원 가입, 토큰 발행, 로그인, 로그아웃")
  @Operation(summary = "회원 가입 요청 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "204", description = "회원가입 성공"),
      @ApiResponse(
        responseCode = "P1",
        description = "400, 파라미터가 비어 있거나 널인 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(
        responseCode = "U4",
        description = "400, 이메일이 중복된 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(
        responseCode = "U5",
        description = "401, 패스워드가 일치하지 않는 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(
        responseCode = "U6",
        description = "400, 패스워드가 4자리 미만인 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(
        responseCode = "U7",
        description = "400, 이메일이 올바르지 않은 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })

  @PostMapping("/api/v1/auth/register")
  public ResponseEntity<Void> register(@RequestBody ReqRegisterUser req,
    HttpServletRequest httpServletRequest) {

    userService.register(req, IpUtils.getClientIp(httpServletRequest));

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Tag(name = "유저 조회, 수정, 삭제 API", description = "정보 조회, 정보 수정, 탈퇴")
  @Operation(summary = "유저 정보 조회 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "유저 정보 조회 성공"),
    })

  @GetMapping("/api/v1/user")
  public ResponseEntity<ResLookUpUser> getUser() {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

    ResLookUpUser resLookUpUser = userService.lookUpPrivacy(
      UUID.fromString(customUserDetails.getId()));

    return new ResponseEntity<>(resLookUpUser, HttpStatus.OK);
  }

  @Tag(name = "유저 조회, 수정, 삭제 API", description = "정보 조회, 정보 수정, 탈퇴")
  @Operation(summary = "유저 정보 조회 관리자 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "유저 정보 조회 성공"),
    })
  @GetMapping("/api/admin/v1/users/{id}")
  public ResponseEntity<ResLookUpUser> getUserByAdmin(@PathVariable String id) {

    ResLookUpUser resLookUpUser = userService.lookUpPrivacy(UUID.fromString(id));

    return new ResponseEntity<>(resLookUpUser, HttpStatus.OK);
  }

  @Tag(name = "유저 조회, 수정, 삭제 API", description = "정보 조회, 정보 수정, 탈퇴")
  @Operation(summary = "모든 유저 정보 조회 관리자 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "유저 정보 조회 성공")})

  @GetMapping("/api/admin/v1/users")
  public ResponseEntity<Page<ResLookUpUsers>> getAllUsers(
    @RequestParam(required = false, defaultValue = "0") int page,
    @RequestParam(required = false, defaultValue = "10") int size) {

    Page<ResLookUpUsers> resLookUpUsers = userService.lookUpUsers(page, size);

    return new ResponseEntity<>(resLookUpUsers, HttpStatus.OK);
  }

  @Tag(name = "유저 조회, 수정, 삭제 API", description = "정보 조회, 정보 수정, 탈퇴")
  @Operation(summary = "유저 정보 수정 API",
    description = "유저 이메일이 변경된 경우 totp 인증도 비활성화 합니다.")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "유저 정보 수정 성공"),
      @ApiResponse(
        responseCode = "E2",
        description = "400, 유효한 인증 정보를 제공하지 않은 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(
        responseCode = "P1",
        description = "400, 파라미터가 비어 있거나 널인 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(
        responseCode = "U7",
        description = "400, 이메일이 올바르지 않은 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(
        responseCode = "U4",
        description = "400, 변경하려는 이메일이 중복된 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })

  @PatchMapping("/api/v1/user/profile")
  public ResponseEntity<ResModifyUser> modifyUserProfile(@PathVariable String id,
    @RequestBody ReqModifyProfile req) {

    ResModifyUser resModifyUser = userService.modifyPrivacy(req, UUID.fromString(id));

    return new ResponseEntity<>(resModifyUser, HttpStatus.OK);
  }

  @Tag(name = "유저 조회, 수정, 삭제 API", description = "정보 조회, 정보 수정, 탈퇴")
  @Operation(summary = "유저 정보 수정 관리자 API",
    description = "유저 이메일이 변경된 경우 totp 인증도 비활성화 합니다.")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "유저 정보 수정 성공"),
      @ApiResponse(
        responseCode = "E2",
        description = "400, 유효한 인증 정보를 제공하지 않은 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(
        responseCode = "P1",
        description = "400, 파라미터가 비어 있거나 널인 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(
        responseCode = "U7",
        description = "400, 이메일이 올바르지 않은 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(
        responseCode = "U4",
        description = "400, 변경하려는 이메일이 중복된 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })

  @PatchMapping("/api/admin/v1/users/{id}/profile")
  public ResponseEntity<ResModifyUser> modifyUserProfileByAdmin(
    @RequestBody ReqModifyProfile req,
    @PathVariable String id) {

    ResModifyUser resModifyUser = userService.modifyPrivacy(req, UUID.fromString(id));

    return new ResponseEntity<>(resModifyUser, HttpStatus.OK);
  }

  @Tag(name = "유저 조회, 수정, 삭제 API", description = "정보 조회, 정보 수정, 탈퇴")
  @Operation(summary = "유저 비빌번호 변경 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "204", description = "유저 비밀번호 변경 성공"),
      @ApiResponse(
        responseCode = "E2",
        description = "400, 유효한 인증 정보를 제공하지 않은 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(
        responseCode = "P1",
        description = "400, 파라미터가 비어 있거나 널인 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(
        responseCode = "U5",
        description = "400, 기존 패스워드가 일치하지 않는 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(
        responseCode = "U6",
        description = "400, 패스워드가 4자리 미만인 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })

  @PatchMapping("/api/v1/user/password")
  public ResponseEntity<Void> modifyUserPassword(@RequestBody ReqModifyPassword req) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

    userService.modifyPassword(req, UUID.fromString(customUserDetails.getId()));

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Tag(name = "유저 조회, 수정, 삭제 API", description = "정보 조회, 정보 수정, 탈퇴")
  @Operation(summary = "유저 비빌번호 변경 관리자 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "204", description = "유저 비밀번호 변경 성공"),
      @ApiResponse(
        responseCode = "P1",
        description = "400, 파라미터가 비어 있거나 널인 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(
        responseCode = "U5",
        description = "400, 기존 패스워드가 일치하지 않는 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(
        responseCode = "U6",
        description = "400, 패스워드가 4자리 미만인 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })

  @PatchMapping("/api/admin/v1/users/{id}/password")
  public ResponseEntity<Void> modifyUserPasswordByAdmin(@PathVariable String id,
    @RequestBody ReqModifyPassword req) {

    userService.modifyPassword(req, UUID.fromString(id));

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Tag(name = "유저 조회, 수정, 삭제 API", description = "정보 조회, 정보 수정, 탈퇴")
  @Operation(summary = "유저 totp 비활성화 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "204", description = "유저 totp 비활성화 성공"),
      @ApiResponse(
        responseCode = "E2",
        description = "400, 유효한 인증 정보를 제공하지 않은 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })

  @PatchMapping("/api/v1/user/totp/disable")
  public ResponseEntity<Void> disableUserTotp() {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

    userService.disableTotp(customUserDetails.getId());

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Tag(name = "유저 조회, 수정, 삭제 API", description = "정보 조회, 정보 수정, 탈퇴")
  @Operation(summary = "유저 탈퇴 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "204", description = "유저 탈퇴 성공"),
      @ApiResponse(
        responseCode = "E2",
        description = "400, 유효한 인증 정보를 제공하지 않은 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })

  @DeleteMapping("/api/v1/user")
  public ResponseEntity<Void> deleteUser() {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

    userService.delete(UUID.fromString(customUserDetails.getId()));

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Tag(name = "유저 조회, 수정, 삭제 API", description = "정보 조회, 정보 수정, 탈퇴")
  @Operation(summary = "유저 탈퇴 관리자 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "204", description = "유저 탈퇴 성공"),
    })
  @DeleteMapping("/api/admin/v1/users/{id}")
  public ResponseEntity<Void> deleteUserByAdmin(@PathVariable String id) {

    userService.delete(UUID.fromString(id));

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
