package com.juny.spacestory.controller;

import com.juny.spacestory.dto.RequestCreateReview;
import com.juny.spacestory.dto.RequestUpdateReview;
import com.juny.spacestory.dto.ResponseReview;
import com.juny.spacestory.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/v1/users/{userId}/reviews")
    public ResponseEntity<List<ResponseReview>> SearchReviewsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<ResponseReview> reviews = reviewService.searchReviewByUserId(userId, page, size);

        return ResponseEntity.ok(reviews);
    }

    @PostMapping("/v1/reviews")
    public ResponseEntity<ResponseReview> create(@RequestBody RequestCreateReview req) {
        ResponseReview createdReview = reviewService.create(req);

        return ResponseEntity.ok(createdReview);
    }

    @PatchMapping("/v1/reviews/{reviewId}")
    public ResponseEntity<ResponseReview> update(@PathVariable Long reviewId, @RequestBody RequestUpdateReview req) {
        ResponseReview updatedReview = reviewService.update(reviewId, req);

        return ResponseEntity.ok(updatedReview);
    }

    @DeleteMapping("v1/reviews/{reviewId}")
    public ResponseEntity<Void> delete(@PathVariable Long reviewId) {
        reviewService.delete(reviewId);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
