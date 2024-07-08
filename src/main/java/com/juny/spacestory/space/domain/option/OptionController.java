package com.juny.spacestory.space.domain.option;

import com.juny.spacestory.global.exception.ErrorResponse;
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
public class OptionController {

  private final OptionService optionService;

  @Tag(name = "옵션 API", description = "옵션 조회, 옵션 추가, 옵션 수정, 옵션 삭제")
  @Operation(summary = "옵션 조회 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "옵션 조회 성공"),
    })

  @GetMapping("/api/v1/options")
  public ResponseEntity<List<ResOption>> findOptions() {

    List<ResOption> options = optionService.findOptions();

    return new ResponseEntity<>(options, HttpStatus.OK);
  }

  @Tag(name = "옵션 API", description = "옵션 조회, 옵션 추가, 옵션 수정, 옵션 삭제")
  @Operation(summary = "옵션 추가 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "옵션 추가 성공"),
    })

  @PostMapping("/api/v1/options")
  public ResponseEntity<ResOption> createOption(@RequestBody  ReqOption req) {

    ResOption option = optionService.createOption(req.name());

    return new ResponseEntity<>(option, HttpStatus.OK);
  }

  @Tag(name = "옵션 API", description = "옵션 조회, 옵션 추가, 옵션 수정, 옵션 삭제")
  @Operation(summary = "옵션 수정 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "옵션 수정 성공"),
      @ApiResponse(
        responseCode = "E2",
        description = "400, 옵션 번호가 잘못된 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })

  @PatchMapping("/api/v1/options/{id}")
  public ResponseEntity<ResOption> modifyOption(@PathVariable Long id, @RequestBody ReqOption req) {

    ResOption option = optionService.modifyOption(id, req.name());

    return new ResponseEntity<>(option, HttpStatus.OK);
  }

  @Tag(name = "옵션 API", description = "옵션 조회, 옵션 추가, 옵션 수정, 옵션 삭제")
  @Operation(summary = "옵션 삭제 API")
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "204", description = "옵션 삭제 성공"),
      @ApiResponse(
        responseCode = "E2",
        description = "400, 옵션 번호가 잘못된 경우",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
  @DeleteMapping("/api/v1/options/{id}")
  public ResponseEntity<Void> deleteOption(@PathVariable Long id) {

    optionService.deleteOption(id);

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
