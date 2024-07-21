package com.juny.spacestory.space.domain.realestate;

import com.juny.spacestory.global.exception.ErrorResponse;
import com.juny.spacestory.global.security.service.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RealEstateController {

  private final RealEstateService realEstateService;

  @Tag(name = "부동산 API", description = "부동산 조회, 부동산 추가, 부동산 수정, 부동산 삭제")
  @Operation(summary = "모든 부동산 조회 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "부동산 조회 성공"),
    })

  @GetMapping("/api/v1/real-estates")
  public ResponseEntity<Page<ResRealEstate>> findRealEstates(
    @RequestParam(required = false, defaultValue = "1") int page,
    @RequestParam(required = false, defaultValue = "10") int size) {

    Page<ResRealEstate> realEstates = realEstateService.findRealEstates(page - 1, size);

    return new ResponseEntity<>(realEstates, HttpStatus.OK);
  }

  @Tag(name = "부동산 API", description = "부동산 조회, 부동산 추가, 부동산 수정, 부동산 삭제")
  @Operation(summary = "호스트가 등록한 부동산 조회 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "부동산 조회 성공"),
      @ApiResponse(
        responseCode = "E2",
        description = "400, 유효한 인증 정보를 제공하지 않은 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })

  @GetMapping("/api/v1/real-estates/host")
  public ResponseEntity<Page<ResRealEstate>> findRealEstatesByUserId(
    @RequestParam(required = false, defaultValue = "1") int page,
    @RequestParam(required = false, defaultValue = "10") int size) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

    Page<ResRealEstate> realEstates = realEstateService.findRealEstatesByUserId(
      UUID.fromString(customUserDetails.getId()), page - 1, size);

    return new ResponseEntity<>(realEstates, HttpStatus.OK);
  }

  @Tag(name = "부동산 API", description = "부동산 조회, 부동산 추가, 부동산 수정, 부동산 삭제")
  @Operation(summary = "부동산 조회 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "단건 부동산 조회 성공"),
      @ApiResponse(
        responseCode = "E2",
        description = "400, 부동산 번호가 잘못된 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })

  @GetMapping("/api/v1/real-estates/{id}")
  public ResponseEntity<ResRealEstate> findRealEstateById(@PathVariable Long id) {

    ResRealEstate realEstate = realEstateService.findRealEstateById(id);

    return new ResponseEntity<>(realEstate, HttpStatus.OK);
  }

  @Tag(name = "부동산 API", description = "부동산 조회, 부동산 추가, 부동산 수정, 부동산 삭제")
  @Operation(summary = "부동산 추가 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "부동산 추가 성공"),
    })

  @PostMapping("/api/v1/real-estates")
  public ResponseEntity<ResRealEstate> createRealEstate(@RequestBody ReqRealEstate req) {

    ResRealEstate realEstate = realEstateService.createRealEstate(req);

    return new ResponseEntity<>(realEstate, HttpStatus.OK);
  }

  @Tag(name = "부동산 API", description = "부동산 조회, 부동산 추가, 부동산 수정, 부동산 삭제")
  @Operation(summary = "부동산 수정 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "부동산 수정 성공"),
      @ApiResponse(
        responseCode = "E2",
        description = "400, 부동산 번호가 잘못된 경우 또는 요청 인자가 유효하지 않은 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })

  @PutMapping("/api/v1/real-estates/{id}")
  public ResponseEntity<ResRealEstate> modifyRealEstate(@PathVariable Long id,
    @RequestBody ReqRealEstate req) {

    ResRealEstate realEstate = realEstateService.updateRealEstate(id, req);

    return new ResponseEntity<>(realEstate, HttpStatus.OK);
  }

  @Tag(name = "부동산 API", description = "부동산 조회, 부동산 추가, 부동산 수정, 부동산 삭제")
  @Operation(summary = "부동산 삭제 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "204", description = "부동산 삭제 성공"),
      @ApiResponse(
        responseCode = "E2",
        description = "400, 부동산 번호가 잘못된 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })

  @DeleteMapping("/api/v1/real-estates/{id}")
  public ResponseEntity<Void> deleteRealEstate(@PathVariable Long id) {

    realEstateService.deleteRealEstate(id);

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
