package com.juny.spacestory.space.controller;

import com.juny.spacestory.space.dto.ResSpace;
import com.juny.spacestory.space.service.SpaceService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class SpaceController {

  private final SpaceService spaceService;

//  @GetMapping("/api/v1/spaces/most-likes")
//  @GetMapping("/api/v1/spaces/most-views")
//  @GetMapping("/api/v1/spaces/most-reviews")
//  @GetMapping("/api/v1/spaces/low_costs")


  @GetMapping("/api/v1/spaces")
  public ResponseEntity<List<ResSpace>> findAllSpaces() {

    List<ResSpace> spaces = spaceService.findAllSpaces();

    return new ResponseEntity<>(spaces, HttpStatus.OK);
  }

  @GetMapping("/api/v1/real_estates/{id}/spaces")
  public ResponseEntity<List<ResSpace>> findSpacesByRealEstateId(@PathVariable Long id) {

    List<ResSpace> spaces = spaceService.findSpacesByRealEstateId(id);

    return new ResponseEntity<>(spaces, HttpStatus.OK);

  }

//  @GetMapping("/api/v1/real_estates/{id}/space/{spaceId}")
//  public ResponseEntity<List<ResSpace>> findSpacesByRealEstateId(@PathVariable Long id) {
//
//    List<ResSpace> spaces = spaceService.findSpacesByRealEstateId(id);
//
//    return new ResponseEntity<>(spaces, HttpStatus.OK);
//
//  }
//  @PostMapping("/api/v1/real_estate/{id}/spaces")
//  @PatchMapping("/api/v1/real_estate/{id}/space/{spaceId}")
//  @DeleteMapping("/api/v1/real_estate/{id}/space/{spaceId}")

//  @GetMapping("/api/v1/spaces/seoul")
//  public ResponseEntity<Page<ResSpaces>> SearchSpacesByTypeInSeoul(
//      @RequestParam SpaceType type,
//      @RequestParam(defaultValue = "0") int page,
//      @RequestParam(defaultValue = "10") int size) {
//    Page<ResSpaces> spaces = spaceService.searchByTypeInSeoul(type, page, size);
//
//    return ResponseEntity.ok(spaces);
//  }
//
//  @GetMapping("/api/v1/spaces")
//  public ResponseEntity<Page<ResSpaces>> searchSpaces(
//      @RequestParam(defaultValue = "FRIENDSHIP") SpaceType type,
//      @RequestParam(defaultValue = "서울특별시") String sido,
//      @RequestParam(defaultValue = "강남구") String sigungu,
//      @RequestParam(defaultValue = "1") int minCapacity,
//      @RequestParam(required = false) Set<DetailedType> detailedType,
//      @RequestParam(defaultValue = "0") int page,
//      @RequestParam(defaultValue = "10") int size) {
//    Page<ResSpaces> resSpaces = spaceService.searchSpaces(type, sido, sigungu, minCapacity,
//      detailedType, page, size);
//
//    return new ResponseEntity<>(resSpaces, HttpStatus.OK);
//  }
//
//  @PostMapping("/api/v1/spaces")
//  public ResponseEntity<ResSpaces> create(@RequestBody RequestCreateSpace req) {
//    ResSpaces createdSpace = spaceService.create(req);
//
//    return ResponseEntity.ok(createdSpace);
//  }
//
//  @PatchMapping("/api/v1/spaces/{spaceId}")
//  public ResponseEntity<ResSpaces> update(
//      @PathVariable Long spaceId, @RequestBody RequestUpdateSpace req) {
//    ResSpaces updatedSpace = spaceService.update(spaceId, req);
//
//    return ResponseEntity.ok(updatedSpace);
//  }
//
//  @DeleteMapping("/api/v1/spaces/{spaceId}")
//  public ResponseEntity<Void> delete(@PathVariable Long spaceId) {
//    spaceService.delete(spaceId);
//
//    return new ResponseEntity<>(HttpStatus.OK);
//  }
}
