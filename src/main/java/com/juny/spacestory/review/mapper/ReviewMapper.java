package com.juny.spacestory.review.mapper;

import com.juny.spacestory.reservation.mapper.ReservationMapper;
import com.juny.spacestory.review.domain.Review;
import com.juny.spacestory.review.dto.ResReview;
import com.juny.spacestory.review.dto.ResUserReview;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring", uses = ReservationMapper.class)
public interface ReviewMapper {

  @Mapping(source = "user.email", target = "email", qualifiedByName = "maskEmail")
  ResReview toResReview(Review review);

  ResUserReview toResUserReview(Review review);

  default Page<ResUserReview> toResUserReviews(Page<Review> reviews) {
    return reviews.map(this::toResUserReview);
  }

  default Page<ResReview> toResReviews(Page<Review> reviews) {
    return reviews.map(this::toResReview);
  }

  @Named("maskEmail")
  default String maskEmail(String userEmail) {

    String[] split = userEmail.split("@");

    int maskLength = split[0].length() / 3;

    Set<Integer> maskPositions = new HashSet<>();
    Random random = new Random();
    while (maskPositions.size() < maskLength) {
      maskPositions.add(random.nextInt(split[0].length()));
    }

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < split[0].length(); i++) {
      if (maskPositions.contains(i)) {
        sb.append('*');
        continue;
      }
      sb.append(split[0].charAt(i));
    }
    sb.append(split[1]);

    return sb.toString();
  }
}
