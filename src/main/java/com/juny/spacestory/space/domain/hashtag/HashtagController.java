package com.juny.spacestory.space.domain.hashtag;

import com.juny.spacestory.global.exception.ErrorCode;
import com.juny.spacestory.global.exception.ErrorResponse;
import com.juny.spacestory.global.exception.common.BadRequestException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HashtagController {

  private final HashtagService hashTagService;

  private final String REQUEST_SEARCH_HASHTAG_PARAMETER_INVALID = "Requested hashtag id or name is empty";

  @Tag(name = "해시태그 API", description = "해시태그 조회, 해시태그 추가, 해시태그 삭제")
  @Operation(summary = "모든 해시태그 조회 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "해시태그 조회 성공"),
    })

  @GetMapping("/api/v1/hashtags")
  public ResponseEntity<Page<ResHashtag>> findAllHashtags(
    @RequestParam(required = false, defaultValue = "1") int page,
    @RequestParam(required = false, defaultValue = "10") int size) {

    Page<ResHashtag> hashtags = hashTagService.findHashtags(page - 1, size);

    return new ResponseEntity<>(hashtags, HttpStatus.OK);
  }


  @Tag(name = "해시태그 API", description = "해시태그 조회, 해시태그 추가, 해시태그 삭제")
  @Operation(summary = "해시태그 조회 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "해시태그 조회 성공"),
      @ApiResponse(
        responseCode = "E2",
        description = "400, 요청 인자가 잘못된 경우 또는 해시태그가 존재하지 않는 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })

  @GetMapping("/api/v1/hashtags/search")
  public ResponseEntity<ResHashtag> findHashtagByIdOrName(
    @RequestParam(required = false) Long id,
    @RequestParam(required = false) String name) {

    ResHashtag hashtag = null;
    if (id != null) {
      hashtag = hashTagService.findHashtagById(id);
      return new ResponseEntity<>(hashtag, HttpStatus.OK);
    }

    if (name != null) {
      hashtag = hashTagService.findHashtagByName(name);
      return new ResponseEntity<>(hashtag, HttpStatus.OK);
    }

    throw new BadRequestException(ErrorCode.BAD_REQUEST, REQUEST_SEARCH_HASHTAG_PARAMETER_INVALID);
  }

  @Tag(name = "해시태그 API", description = "해시태그 조회, 해시태그 추가, 해시태그 삭제")
  @Operation(summary = "해시태그 추가 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "해시태그 추가 성공")
    })

  @PostMapping("/api/v1/hashtags")
  public ResponseEntity<ResHashtag> createHashtag(@RequestBody ReqHashtag req) {

    ResHashtag hashtag = hashTagService.createHashtag(req.name());

    return new ResponseEntity<>(hashtag, HttpStatus.OK);
  }

  @Tag(name = "해시태그 API", description = "해시태그 조회, 해시태그 추가, 해시태그 삭제")
  @Operation(summary = "해시태그 조회 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "해시태그 조회 성공"),
      @ApiResponse(
        responseCode = "E2",
        description = "400, 요청 인자가 잘못된 경우 또는 해시태그가 존재하지 않는 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })

  @DeleteMapping("/api/v1/hashtags")
  public ResponseEntity<Void> deleteHashtagByIdOrName(
    @RequestParam(required = false) Long id,
    @RequestParam(required = false) String name) {

    if (id != null) {
      hashTagService.deleteHashtagById(id);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    if (name != null) {
      hashTagService.deleteHashtagByName(name);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    throw new BadRequestException(ErrorCode.BAD_REQUEST, REQUEST_SEARCH_HASHTAG_PARAMETER_INVALID);
  }
}
