package com.juny.spacestory.controller;

import com.juny.spacestory.domain.DetailedType;
import com.juny.spacestory.domain.SpaceType;
import com.juny.spacestory.dto.RequestCreateSpace;
import com.juny.spacestory.dto.RequestUpdateSpace;
import com.juny.spacestory.dto.ResponseSpace;
import com.juny.spacestory.service.SpaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class SpaceController {

    private final SpaceService spaceService;

    @GetMapping("/v1/spaces/seoul")
    public ResponseEntity<List<ResponseSpace>> SearchSpacesByTypeInSeoul(
            @RequestParam SpaceType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<ResponseSpace> spaces = spaceService.searchByTypeInSeoul(type, page, size);

        return ResponseEntity.ok(spaces);
    }

    @GetMapping("/v1/spaces")
    public ResponseEntity<List<ResponseSpace>> searchSpaces(
            @RequestParam(defaultValue = "FRIENDSHIP") SpaceType type,
            @RequestParam(defaultValue = "서울특별시") String sido,
            @RequestParam(defaultValue = "강남구") String sigungu,
            @RequestParam(defaultValue = "1") int minCapacity,
            @RequestParam(required = false) Set<DetailedType> detailedType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<ResponseSpace> spaces = spaceService.searchSpaces(type, sido, sigungu, minCapacity, detailedType, page, size);

        return ResponseEntity.ok(spaces);
    }

    @PostMapping("/v1/spaces")
    public ResponseEntity<ResponseSpace> create(@RequestBody RequestCreateSpace req) {
        ResponseSpace createdSpace = spaceService.create(req);

        return ResponseEntity.ok(createdSpace);
    }

    @PatchMapping("/v1/spaces/{spaceId}")
    public ResponseEntity<ResponseSpace> update(@PathVariable Long spaceId, @RequestBody RequestUpdateSpace req) {
        ResponseSpace updatedSpace = spaceService.update(spaceId, req);

        return ResponseEntity.ok(updatedSpace);
    }

    @DeleteMapping("/v1/spaces/{spaceId}")
    public ResponseEntity<Void> delete(@PathVariable Long spaceId) {
        spaceService.delete(spaceId);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}