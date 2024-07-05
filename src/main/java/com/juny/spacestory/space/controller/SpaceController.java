package com.juny.spacestory.space.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class SpaceController {

//  private final SpaceService spaceService;
//
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
