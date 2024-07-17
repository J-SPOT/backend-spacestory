package com.juny.spacestory.review.repository;

import com.juny.spacestory.review.domain.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomReviewRepository {

  Page<Review> findReviewsBySpaceId(Long spaceId, Pageable pageable);
}
