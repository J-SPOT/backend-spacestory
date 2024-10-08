package com.juny.spacestory.review.domain;

import com.juny.spacestory.reservation.entity.Reservation;
import com.juny.spacestory.review.dto.ReqReview;
import com.juny.spacestory.user.domain.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "reviews")
public class Review {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String content;

  @Column(nullable = false)
  private Double rating;

  @Column
  private LocalDateTime createdAt;

  @Column
  private LocalDateTime deletedAt;

  @ElementCollection
  @CollectionTable(name = "review_images", joinColumns = @JoinColumn(name = "review_id"))
  @Column(name = "image_path")
  private List<String> imagePaths = new ArrayList<>();

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @OneToOne
  @JoinColumn(name = "reservation_id", nullable = false, unique = true)
  private Reservation reservation;

  public Review(String content, Double rating) {
    this.content = content;
    this.rating = rating;
    this.createdAt = LocalDateTime.now();
  }

  // ManyToOne 연관관계 편의 메서드, 리뷰 - 유저 [양방향]
  public void setUser(User user) {
    if (this.user != null) {
      this.user.getReviews().remove(this);
    }
    this.user = user;
    if (user != null && !user.getReviews().contains(this)) {
      user.getReviews().add(this);
    }
  }

  // OneToOne 연관관계 편의 메서드, 리뷰 - 예약 [단방향]
  public void setReservation(Reservation reservation) {
    this.reservation = reservation;
  }

  public void update(ReqReview req) {

    this.content = req.content();
    this.rating = req.rating();
  }

  public void softDelete() {

    this.deletedAt = LocalDateTime.now();
  }
}
