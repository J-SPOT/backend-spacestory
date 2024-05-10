package com.juny.spacestory.service;

import com.juny.spacestory.domain.Review;
import com.juny.spacestory.domain.User;
import com.juny.spacestory.dto.RequestCreateReview;
import com.juny.spacestory.dto.RequestUpdateReview;
import com.juny.spacestory.dto.ResponseReview;
import com.juny.spacestory.exception.global.ErrorCode;
import com.juny.spacestory.exception.review.ReviewInvalidIdException;
import com.juny.spacestory.exception.user.UserInvalidIdException;
import com.juny.spacestory.mapper.ReviewMapper;
import com.juny.spacestory.repository.ReviewRepository;
import com.juny.spacestory.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private final UserRepository userRepository;

    private final ReviewMapper mapper;

    public List<ResponseReview> searchReviewByUserId(Long userId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        List<Review> reviews = reviewRepository.findByUserIdAndIsDeletedFalse(userId, pageRequest).getContent();
        return mapper.ReviewsToResponseReviews(reviews);
    }

    public ResponseReview create(RequestCreateReview req) {
        User user = findByUserId(req.userId());
        Review review = new Review(req.content(), req.rating(), req.userId(), false);
        Review savedReview = reviewRepository.save(review);

        return mapper.ReviewToResponseReview(savedReview);
    }

    public ResponseReview update(Long reviewId, RequestUpdateReview req) {
        Review review = findByReviewId(reviewId);
        review.update(req);
        Review updatedReview = reviewRepository.save(review);

        return mapper.ReviewToResponseReview(updatedReview);
    }

    public void delete(Long reviewId) {
        Review review = findByReviewId(reviewId);
        review.softDelete(review);
        reviewRepository.save(review);
    }

    private User findByUserId(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserInvalidIdException(ErrorCode.USER_INVALID_ID));
    }

    private Review findByReviewId(Long reviewId) {
        return reviewRepository.findById(reviewId).orElseThrow(() -> new ReviewInvalidIdException(ErrorCode.REVIEW_INVALID_ID));
    }
}
