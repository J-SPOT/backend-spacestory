package com.juny.spacestory.review.domain;

import com.juny.spacestory.global.exception.ErrorCode;
import com.juny.spacestory.global.exception.hierarchy.review.ReviewInvalidIdBusinessException;
import com.juny.spacestory.global.exception.hierarchy.user.UserInvalidIdBusinessException;
import com.juny.spacestory.review.mapper.ReviewMapper;
import com.juny.spacestory.review.repository.ReviewRepository;
import com.juny.spacestory.review.dto.RequestCreateReview;
import com.juny.spacestory.review.dto.RequestUpdateReview;
import com.juny.spacestory.review.dto.ResponseReview;
import com.juny.spacestory.user.domain.User;
import com.juny.spacestory.user.repository.UserRepository;
import jakarta.persistence.*;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Review {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String content;

  @Column(nullable = false)
  private Double rating;

  @Column(nullable = false)
  private Long userId;

  @Column(nullable = false)
  private Boolean isDeleted;

  private String imgPath;

  public Review(String content, Double rating, Long userId, Boolean isDeleted) {
    this.content = content;
    this.rating = rating;
    this.userId = userId;
    this.isDeleted = isDeleted;
  }

  public void update(RequestUpdateReview req) {
    this.content = req.content();
    this.rating = req.rating();
  }

  public void softDelete(Review review) {
    this.isDeleted = true;
  }
}
