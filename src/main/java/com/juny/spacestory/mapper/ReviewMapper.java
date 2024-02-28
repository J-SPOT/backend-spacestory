package com.juny.spacestory.mapper;

import com.juny.spacestory.domain.Review;
import com.juny.spacestory.dto.ResponseReview;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    ResponseReview ReviewToResponseReview(Review review);

    List<ResponseReview> ReviewsToResponseReviews(List<Review> reviews);
}
