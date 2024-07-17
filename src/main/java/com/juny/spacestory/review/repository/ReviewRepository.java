package com.juny.spacestory.review.repository;

import com.juny.spacestory.review.domain.Review;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long>, CustomReviewRepository {

  Page<Review> findAllByUserIdAndDeletedAtIsNull(UUID userId, Pageable pageable);
}
