package com.juny.spacestory.space.controller;

import com.juny.spacestory.global.exception.ErrorResponse;
import com.juny.spacestory.space.dto.ReqCategoryName;
import com.juny.spacestory.space.dto.ResCategory;
import com.juny.spacestory.space.dto.ResMainCategory;
import com.juny.spacestory.space.dto.ResSubCategory;
import com.juny.spacestory.space.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CategoryController {

  private final CategoryService categoryService;

  @Tag(name = "카테고리 API", description = "카테고리 조회, 카테고리 추가, 카테고리 수정, 카테고리 삭제")
  @Operation(summary = "전체 카테고리 조회 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "전체 카테고리 조회 성공"),
    })

  @GetMapping("/api/v1/categories")
  public ResponseEntity<List<ResCategory>> findCategories() {

    List<ResCategory> categories = categoryService.findCategories();

    return new ResponseEntity<>(categories, HttpStatus.OK);
  }

  @Tag(name = "카테고리 API", description = "카테고리 조회, 카테고리 추가, 카테고리 수정, 카테고리 삭제")
  @Operation(summary = "모든 대분류(메인) 카테고리 조회 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "대분류(메인) 카테고리 조회 성공"),
    })

  @GetMapping("/api/v1/categories/main")
  public ResponseEntity<List<ResMainCategory>> findMainCategories() {

    List<ResMainCategory> mainCategories = categoryService.findMainCategories();

    return new ResponseEntity<>(mainCategories, HttpStatus.OK);
  }

  @Tag(name = "카테고리 API", description = "카테고리 조회, 카테고리 추가, 카테고리 수정, 카테고리 삭제")
  @Operation(summary = "대분류(메인) 카테고리 단건 조회 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "대분류(메인) 카테고리 단건 조회 성공"),
      @ApiResponse(
        responseCode = "E2",
        description = "400, 대분류(메인) 카테고리 번호가 잘못된 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })

  @GetMapping("/api/v1/categories/main/{id}")
  public ResponseEntity<ResMainCategory> findMainCategoryById(@PathVariable Long id) {

    ResMainCategory mainCategory = categoryService.findMainCategoryById(id);

    return new ResponseEntity<>(mainCategory, HttpStatus.OK);
  }

  @Tag(name = "카테고리 API", description = "카테고리 조회, 카테고리 추가, 카테고리 수정, 카테고리 삭제")
  @Operation(summary = "대분류(메인) 카테고리 추가 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "대분류(메인) 카테고리 추가 성공"),
    })

  @PostMapping("/api/v1/categories/main")
  public ResponseEntity<ResMainCategory> createMainCategory(@RequestBody ReqCategoryName req) {

    ResMainCategory mainCategory = categoryService.createMainCategory(req.name());

    return new ResponseEntity<>(mainCategory, HttpStatus.OK);
  }

  @Tag(name = "카테고리 API", description = "카테고리 조회, 카테고리 추가, 카테고리 수정, 카테고리 삭제")
  @Operation(summary = "대분류(메인) 카테고리 수정 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "대분류(메인) 카테고리 수정 성공"),
      @ApiResponse(
        responseCode = "E2",
        description = "400, 대분류(메인) 카테고리 번호가 잘못된 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })

  @PatchMapping("/api/v1/categories/main/{id}")
  public ResponseEntity<ResMainCategory> modifyMainCategory(@PathVariable Long id,
    @RequestBody ReqCategoryName req) {

    ResMainCategory mainCategory = categoryService.modifyMainCategoryById(id, req.name());

    return new ResponseEntity<>(mainCategory, HttpStatus.OK);
  }

  @Tag(name = "카테고리 API", description = "카테고리 조회, 카테고리 추가, 카테고리 수정, 카테고리 삭제")
  @Operation(summary = "대분류(메인) 카테고리 삭제 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "204", description = "대분류(메인) 카테고리 삭제 성공"),
      @ApiResponse(
        responseCode = "E2",
        description = "400, 대분류(메인) 카테고리 번호가 잘못된 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })

  @DeleteMapping("/api/v1/categories/main/{id}")
  public ResponseEntity<Void> deleteMainCategory(@PathVariable Long id) {

    categoryService.deleteById(id);

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Tag(name = "카테고리 API", description = "카테고리 조회, 카테고리 추가, 카테고리 수정, 카테고리 삭제")
  @Operation(summary = "대분류(메인) 카테고리에 속하는 모든 중분류(서브) 카테고리 조회 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "모든 중분류(서브) 카테고리 조회 성공"),
      @ApiResponse(
        responseCode = "E2",
        description = "400, 대분류(메인) 카테고리 번호가 잘못된 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })

  @GetMapping("/api/v1/categories/main/{id}/sub")
  public ResponseEntity<List<ResSubCategory>> findSubCategoryByMainCategoryId(@PathVariable Long id) {

    List<ResSubCategory> subCategories = categoryService.findSubCategoriesByMainCategoryId(id);

    return new ResponseEntity<>(subCategories, HttpStatus.OK);
  }


  @Tag(name = "카테고리 API", description = "카테고리 조회, 카테고리 추가, 카테고리 수정, 카테고리 삭제")
  @Operation(summary = "중분류(서브) 카테고리 단건 조회 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "중분류(서브) 카테고리 단건 조회 성공"),
      @ApiResponse(
        responseCode = "E2",
        description = "400, 대분류(메인) 카테고리 번호가 잘못된 경우<br>400, 중분류(서브)카테고리가 대분류 카테고리에 속하지 않는 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })

  @GetMapping("/api/v1/categories/main/{id}/sub/{sub_id}")
  public ResponseEntity<ResSubCategory> findSubCategoryById(@PathVariable Long id, @PathVariable(value = "sub_id") Long subId) {

    ResSubCategory subCategory = categoryService.findSubCategoryById(id, subId);

    return new ResponseEntity<>(subCategory, HttpStatus.OK);
  }

  @Tag(name = "카테고리 API", description = "카테고리 조회, 카테고리 추가, 카테고리 수정, 카테고리 삭제")
  @Operation(summary = "중분류(서브) 카테고리 추가 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "중분류(서브) 카테고리 추가 성공"),
      @ApiResponse(
        responseCode = "E2",
        description = "400, 대분류(메인) 카테고리 번호가 잘못된 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })

  @PostMapping("/api/v1/categories/main/{id}/sub")
  public ResponseEntity<ResSubCategory> creteSubCategory(@PathVariable Long id, @RequestBody ReqCategoryName req) {

    ResSubCategory subCategory = categoryService.createSubCategory(id, req.name());

    return new ResponseEntity<>(subCategory, HttpStatus.OK);
  }

  @Tag(name = "카테고리 API", description = "카테고리 조회, 카테고리 추가, 카테고리 수정, 카테고리 삭제")
  @Operation(summary = "중분류(서브) 카테고리 수정 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "중분류(서브) 카테고리 수정 성공"),
      @ApiResponse(
        responseCode = "E2",
        description = "400, 대분류(메인) 카테고리 번호가 잘못된 경우<br>400, 중분류(서브)카테고리가 대분류 카테고리에 속하지 않는 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })

  @PatchMapping("/api/v1/categories/main/{id}/sub/{sub_id}")
  public ResponseEntity<ResSubCategory> modifySubCategory(@PathVariable Long id,
    @PathVariable(value = "sub_id") Long subId, @RequestBody ReqCategoryName req) {

    ResSubCategory subCategory = categoryService.modifySubCategory(id, subId, req.name());

    return new ResponseEntity<>(subCategory, HttpStatus.OK);
  }

  @Tag(name = "카테고리 API", description = "카테고리 조회, 카테고리 추가, 카테고리 수정, 카테고리 삭제")
  @Operation(summary = "중분류(서브) 카테고리 삭제 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "중분류(서브) 카테고리 삭제 성공"),
      @ApiResponse(
        responseCode = "E2",
        description = "400, 대분류(메인) 카테고리 번호가 잘못된 경우<br>400, 중분류(서브)카테고리가 대분류 카테고리에 속하지 않는 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })

  @DeleteMapping("/api/v1/categories/main/{id}/sub/{sub_id}")
  public ResponseEntity<ResSubCategory> deleteSubCategory(@PathVariable Long id,
    @PathVariable(value = "sub_id") Long subId) {

    categoryService.deleteSubCategory(id, subId);

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
