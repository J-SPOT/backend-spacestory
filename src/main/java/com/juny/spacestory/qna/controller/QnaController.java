package com.juny.spacestory.qna.controller;

import com.juny.spacestory.global.exception.ErrorResponse;
import com.juny.spacestory.global.security.service.CustomUserDetails;
import com.juny.spacestory.qna.dto.ReqAnswer;
import com.juny.spacestory.qna.dto.ReqQuestion;
import com.juny.spacestory.qna.dto.ResAnswer;
import com.juny.spacestory.qna.dto.ResQuestion;
import com.juny.spacestory.qna.service.QnaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class QnaController {

  private final QnaService qnaService;

  @Tag(name = "Qna API", description = "Qna 조회, Qna 추가, Qna 수정, Qna 삭제")
  @Operation(summary = "Qna 조회 API", description = "공간에 관련된 모든 Qna 질문과 답글을 가져옵니다.")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "Qna 조회 성공"),
    })

  @GetMapping("/api/v1/spaces/{id}/qna")
  public ResponseEntity<Page<ResQuestion>> findQuestionsAndAnswers(
    @RequestParam(required = false, defaultValue = "1") int page,
    @RequestParam(required = false, defaultValue = "10") int size,
    @PathVariable Long id) {

    Page<ResQuestion> questions = qnaService.findQuestionsAndAnswers(id, page - 1, size);

    return new ResponseEntity<>(questions, HttpStatus.OK);
  }

  @Tag(name = "Qna API", description = "Qna 조회, Qna 추가, Qna 수정, Qna 삭제")
  @Operation(summary = "질문 추가 API", description = "공간에 관한 질문을 작성합니다.")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "질문 추가 성공"),
      @ApiResponse(
        responseCode = "E2",
        description = "400, 유효한 인증 정보를 제공하지 않은 경우<br>400, 유효한 공간 아이디가 아닌 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })

  @PostMapping("/api/v1/spaces/{id}/questions")
  public ResponseEntity<ResQuestion> createQuestion(
    @PathVariable Long id, @RequestBody ReqQuestion req) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

    ResQuestion question = qnaService.createQuestion(
      id, req, UUID.fromString(customUserDetails.getId()));

    return new ResponseEntity<>(question, HttpStatus.OK);
  }

  @Tag(name = "Qna API", description = "Qna 조회, Qna 추가, Qna 수정, Qna 삭제")
  @Operation(summary = "답변 추가 API", description = "질문에 대한 답변을 작성하고, 답변에 관한 답변을 작성할 수 있습니다.")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "답변 추가 성공"),
      @ApiResponse(
        responseCode = "E2",
        description = "400, 유효한 인증 정보를 제공하지 않은 경우<br>400, 유효한 질문 아이디가 아닌 경우<br>400, 첫 번째 답글이 호스트가 아닌 경우<br>400, 답글의 부모가 올바르지 않은 경우<br>400, 답글의 부모와 현재 답글 유저가 같은 경우<br>400, 이미 답글을 달았는 데, 또 답글을 작성하는 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })

  @PostMapping("/api/v1/questions/{id}/answers")
  public ResponseEntity<ResAnswer> createAnswer(
    @PathVariable Long id, @RequestBody ReqAnswer req) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

    ResAnswer answer = qnaService.createAnswer(
      id, req, UUID.fromString(customUserDetails.getId()));

    return new ResponseEntity<>(answer, HttpStatus.OK);
  }

  @Tag(name = "Qna API", description = "Qna 조회, Qna 추가, Qna 수정, Qna 삭제")
  @Operation(summary = "질문 수정 API", description = "질문을 수정합니다. 답글이 작성되기 전에만 수정할 수 있습니다.")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "질문 수정 성공"),
      @ApiResponse(
        responseCode = "E2",
        description = "400, 유효한 인증 정보를 제공하지 않은 경우<br>400, 유효한 질문 아이디가 아닌 경우<br>400, 질문 작성자가 아닌 경우<br>400, 이미 답변이 달린 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })

  @PatchMapping("/api/v1/questions/{id}")
  public ResponseEntity<ResQuestion> updateQuestion(
    @PathVariable Long id, @RequestBody ReqQuestion req) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

    ResQuestion question = qnaService.updateQuestion(id, req,
      UUID.fromString(customUserDetails.getId()));

    return new ResponseEntity<>(question, HttpStatus.OK);
  }

  @Tag(name = "Qna API", description = "Qna 조회, Qna 추가, Qna 수정, Qna 삭제")
  @Operation(summary = "답변 수정 API", description = "답변을 수정합니다. 답변이 작성되기 전에만 수정할 수 있습니다.")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "답변 수정 성공"),
      @ApiResponse(
        responseCode = "E2",
        description = "400, 유효한 인증 정보를 제공하지 않은 경우<br>400, 유효한 답변 아이디가 아닌 경우<br>400, 답변 작성자가 아닌 경우<br>400, 이미 답변이 달린 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })

  @PatchMapping("/api/v1/answers/{id}")
  public ResponseEntity<ResAnswer> updateAnswer(
    @PathVariable Long id, @RequestBody ReqAnswer req) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

    ResAnswer answer = qnaService.updateAnswer(id, req,
      UUID.fromString(customUserDetails.getId()));

    return new ResponseEntity<>(answer, HttpStatus.OK);
  }
}
