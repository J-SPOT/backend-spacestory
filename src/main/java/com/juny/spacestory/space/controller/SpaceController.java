package com.juny.spacestory.space.controller;

import com.juny.spacestory.global.exception.ErrorResponse;
import com.juny.spacestory.global.security.service.CustomUserDetails;
import com.juny.spacestory.space.domain.Space;
import com.juny.spacestory.space.dto.ReqSpace;
import com.juny.spacestory.space.dto.ResSpace;
import com.juny.spacestory.space.dto.ResSummarySpace;
import com.juny.spacestory.space.repository.mybatis.SpaceMapper;
import com.juny.spacestory.space.service.SpaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class SpaceController {

  private final SpaceService spaceService;

  private final SpaceMapper mapper;

  @GetMapping("/api/v1/spaces/mybatis")
  public ResponseEntity<Page<Space>> findAllSpacesByMybatis(
    @RequestParam(required = false, defaultValue = "1") int page,
    @RequestParam(required = false, defaultValue = "10") int size) {

    int totalCount = mapper.countAll();

    int offset = (page - 1) * size;

    List<Long> spaceIds = mapper.findSpaceIds(size, offset);

    List<Space> spaces = mapper.selectSpacesWithOptions(spaceIds);

    PageRequest pageRequest = PageRequest.of(page - 1, size);

    return new ResponseEntity<>(new PageImpl<>(spaces, pageRequest, totalCount), HttpStatus.OK);
  }

  @Tag(name = "공간 API", description = "공간 조회, 공간 추가, 공간 수정, 공간 삭제")
  @Operation(summary = "메인 페이지 공간 요약 정보 조회 API", description = "정렬 조건을 쿼리 파라미터로 제공합니다. <br>1. created-desc(최근 생성 기준, 기본값)<br>2. view-desc(가장 많은 조회수 기준)<br>3. like-desc(가장 많은 좋아요 기준)")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "공간 조회 성공"),
    })

  @GetMapping("/api/v1/spaces")
  public ResponseEntity<Page<ResSummarySpace>> findSortedSpacesForMainPage(
    @RequestParam(required = false, defaultValue = "1") int page,
    @RequestParam(required = false, defaultValue = "10") int size,
    @RequestParam(required = false, defaultValue = "created-desc") String sort) {
    
    Page<ResSummarySpace> spaces = spaceService.findSortedSpacesForMainPage(page - 1, size, sort);

    return new ResponseEntity<>(spaces, HttpStatus.OK);
  }

  @Tag(name = "공간 API", description = "공간 조회, 공간 추가, 공간 수정, 공간 삭제")
  @Operation(
    summary = "공간 상세 정보 조회 API",
    description = "해당 API를 호출하면 공간 조회수가 증가합니다.")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "공간 조회 성공"),
      @ApiResponse(
        responseCode = "E2",
        description = "400, 공간 번호가 잘못된 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })

  @GetMapping("/api/v1/spaces/{id}")
  public ResponseEntity<ResSpace> findSpaceById(@PathVariable Long id) {

    ResSpace space = spaceService.findSpaceById(id);

    return new ResponseEntity<>(space, HttpStatus.OK);
  }

  @Tag(name = "공간 API", description = "공간 조회, 공간 추가, 공간 수정, 공간 삭제")
  @Operation(
    summary = "특정 부동산에 있는 모든 공간 조회 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "공간 조회 성공"),
      @ApiResponse(
        responseCode = "E2",
        description = "400, 부동산 번호가 잘못된 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })

  @GetMapping("/api/v1/real-estates/{id}/spaces")
  public ResponseEntity<Page<ResSpace>> findSpacesByRealEstateId(@PathVariable Long id,
    @RequestParam(required = false, defaultValue = "1") int page,
    @RequestParam(required = false, defaultValue = "10") int size) {

    Page<ResSpace> spaces = spaceService.findSpacesByRealEstateId(id, page - 1, size);

    return new ResponseEntity<>(spaces, HttpStatus.OK);
  }

  @Tag(name = "공간 API", description = "공간 조회, 공간 추가, 공간 수정, 공간 삭제")
  @Operation(
    summary = "공간 추가 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "공간 추가 성공"),
      @ApiResponse(
        responseCode = "E2",
        description = "400, 부동산 번호, 카테고리 또는 옵션 이름이 잘못된 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })

  @PostMapping("/api/v1/real-estates/{id}/spaces")
  public ResponseEntity<ResSpace> createSpace(@PathVariable Long id, @RequestBody ReqSpace req) {

    ResSpace space = spaceService.createSpace(id, req);

    return new ResponseEntity<>(space, HttpStatus.OK);
  }

  @Tag(name = "공간 API", description = "공간 조회, 공간 추가, 공간 수정, 공간 삭제")
  @Operation(
    summary = "공간 수정 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "공간 수정 성공"),
      @ApiResponse(
        responseCode = "E2",
        description = "400, 공간 번호, 카테고리 또는 옵션 이름이 잘못된 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })

  @PutMapping("/api/v1/spaces/{id}")
  public ResponseEntity<ResSpace> modifySpace(
    @PathVariable Long id,
    @RequestBody ReqSpace req) {

    ResSpace space = spaceService.updateSpace(id, req);

    return new ResponseEntity<>(space, HttpStatus.OK);
  }

  @Tag(name = "공간 API", description = "공간 조회, 공간 추가, 공간 수정, 공간 삭제")
  @Operation(
    summary = "공간 삭제 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "204", description = "공간 삭제 성공"),
      @ApiResponse(
        responseCode = "E2",
        description = "400, 공간 번호가 잘못된 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })

  @DeleteMapping("/api/v1/spaces/{id}")
  public ResponseEntity<Void> deleteSpace(@PathVariable Long id) {

    spaceService.deleteSpace(id);

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Tag(name = "공간 API", description = "공간 조회, 공간 추가, 공간 수정, 공간 삭제")
  @Operation(
    summary = "공간 조회 API 상세 검색 필터",
    description = "query는 공간 이름과 공간 타입을 검색한다.<br>sort 인자로 view_desc(조회수 내림차순), price_asc(가격 오름차순), price_desc(가격 내림차순), review_desc(리뷰 내림차순), like_desc(좋아요 내림차순)")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "공간 조회 성공"),
      @ApiResponse(
        responseCode = "E2",
        description = "400, 시군구 3개 초과 선택",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })

  @GetMapping("/api/v1/spaces/search")
  @Transactional
  public ResponseEntity<Page<ResSummarySpace>> searchSpaces(
    @RequestParam(required = false) String query,
    @RequestParam(required = false) List<String> sigungu,
    @RequestParam(required = false, value = "min-capacity") Integer minCapacity,
    @RequestParam(required = false, value = "min-price") Integer minPrice,
    @RequestParam(required = false, value = "max-price") Integer maxPrice,
    @RequestParam(required = false) List<String> options,
    @RequestParam(required = false, defaultValue = "view-desc") String sort,
    @RequestParam(required = false, defaultValue = "1") int page,
    @RequestParam(required = false, defaultValue = "10") int size) {

    Page<ResSummarySpace> spaces = spaceService.searchSpaces(
      query, sigungu, minCapacity, minPrice,
      maxPrice, options, sort, page - 1, size);

    return new ResponseEntity<>(spaces, HttpStatus.OK);
  }

  @Tag(name = "공간 API", description = "공간 조회, 공간 추가, 공간 수정, 공간 삭제")
  @Operation(
    summary = "공간 이미지 추가 API",
    description = "공간마다 최대 10개의 이미지를 올릴 수 있습니다.<br>Content-Type: multipart/form-data<br>이미지 업로드, 삭제 API는 이미지 경로를 모두 요청 파라미터로 받습니다.")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "공간 이미지 추가 성공"),
      @ApiResponse(
        responseCode = "E2",
        description = "400, 유효한 인증 정보를 제공하지 않은 경우<br>400, 유효하지 않은 공간 아이디인 경우, <br>400, 이미지를 10개 초과해서 업로드 하려는 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })

  @PostMapping("/api/v1/spaces/{id}/images")
  public ResponseEntity<ResSpace> uploadImages(
    @PathVariable Long id,
    @RequestParam List<MultipartFile> files) throws IOException {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

    ResSpace space = spaceService.uploadImages(id, UUID.fromString(customUserDetails.getId()), files);

    return new ResponseEntity<>(space, HttpStatus.OK);
  }

  @Tag(name = "공간 API", description = "공간 조회, 공간 추가, 공간 수정, 공간 삭제")
  @Operation(
    summary = "공간 이미지 삭제 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "204", description = "공간 이미지 삭제 성공"),
      @ApiResponse(
        responseCode = "E2",
        description = "400, 유효한 인증 정보를 제공하지 않은 경우<br>400, 유효하지 않은 공간 아이디인 경우, <br>400, 유효하지 않은 이미지 경로인 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })

  @DeleteMapping("/api/v1/spaces/{id}/images")
  public ResponseEntity<Void> deleteImage(
    @PathVariable Long id,
    @RequestParam List<String> imagePath) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

    spaceService.deleteImage(id, UUID.fromString(customUserDetails.getId()), imagePath);

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Tag(name = "공간 API", description = "공간 조회, 공간 추가, 공간 수정, 공간 삭제")
  @Operation(
    summary = "공간 대표 이미지 설정 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "공간 대표 이미지 설정 성공"),
      @ApiResponse(
        responseCode = "E2",
        description = "400, 유효한 인증 정보를 제공하지 않은 경우<br>400, 유효하지 않은 공간 아이디인 경우, <br>400, 유효하지 않은 이미지 경로인 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })

  @PatchMapping("/api/v1/spaces/{id}/images/represent")
  public ResponseEntity<ResSpace> setRepresentImage(
    @PathVariable Long id,
    @RequestParam String imagePath) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

    ResSpace space = spaceService.setRepresentImage(id, UUID.fromString(customUserDetails.getId()),
      imagePath);

    return new ResponseEntity<>(space, HttpStatus.OK);
  }
}
