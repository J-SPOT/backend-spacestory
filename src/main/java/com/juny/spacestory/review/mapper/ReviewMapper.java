package com.juny.spacestory.review.mapper;

import com.juny.spacestory.review.domain.Review;
import com.juny.spacestory.review.dto.ResponseReview;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
  ResponseReview ReviewToResponseReview(Review review);

  List<ResponseReview> ReviewsToResponseReviews(List<Review> reviews);
}
