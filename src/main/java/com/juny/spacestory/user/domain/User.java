package com.juny.spacestory.user.domain;

import com.juny.spacestory.audit.SoftDeleteBaseEntity;
import com.juny.spacestory.global.exception.ErrorCode;
import com.juny.spacestory.global.exception.hierarchy.user.UserExceededPointBusinessException;
import com.juny.spacestory.point.domain.Point;
import com.juny.spacestory.qna.domain.Answer;
import com.juny.spacestory.qna.domain.Question;
import com.juny.spacestory.reservation.entity.Reservation;
import com.juny.spacestory.review.domain.Review;
import com.juny.spacestory.user.dto.ReqModifyProfile;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "users")
public class User extends SoftDeleteBaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(columnDefinition = "BINARY(16)")
  private UUID id;

  @Enumerated(EnumType.STRING)
  private Role role;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, unique = true)
  private String email;

  @Column
  private String phoneNumber;

  @Column
  private String password;

  @Column
  private Integer point;

  @Column
  private String socialId;

  @Column
  private Boolean isTotpEnabled;

  @Column
  private Integer currentPoints;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "user_ip_addresses", joinColumns = @JoinColumn(name = "user_id"))
  @Column(name = "ip_address")
  private List<String> ipAddresses = new ArrayList<>();

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "host_id")
  private Host host;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Point> points = new ArrayList<>();

  @OneToMany(mappedBy = "user")
  private List<Reservation> reservations = new ArrayList<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Review> reviews;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Question> questions = new ArrayList<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Answer> answers = new ArrayList<>();

  // 일반 로그인
  public User(String name, String email, String password, String ip) {
    this.name = name;
    this.email = email;
    this.password = password;
    this.point = 0;
    this.role = Role.USER;
    this.ipAddresses.add(ip);
    this.isTotpEnabled = false;
  }

  // 소셜 로그인
  public User(String name, String email, Role role, String socialId) {
    this.name = name;
    this.email = email;
    this.point = 0;
    this.role = role;
    this.socialId = socialId;
    this.isTotpEnabled = false;
  }

  // jwt토큰으로 SecurityContextHolder에 저장
  public User(UUID id, Role role) {
    this.id = id;
    this.role = role;
  }

  // OneToOne 연관관계 편의 메서드, 유저 - 호스트 [양방향]
  public void setHost(Host host) {
    this.host = host;
    if (host != null) {
      host.setUser(this);
    }
  }

  // OneToMany 연관관계 편의 메서드, 유저 - 포인트 [단방향]
  public void addPoint(Point point) {
    points.add(point);
    currentPoints += point.getAmount();
  }

  // OneToMany 연관관계 편의 메서드, 유저 - 포인트 [단방향]
  public void subtractPoint(Point point) {
    points.remove(point);
    currentPoints -= point.getAmount();
  }

  // OneToMany 연관관계 편의 메서드, 유저 - 리뷰 [양방향]
  public void addReview(Review review) {
    this.reviews.add(review);
    if (review.getUser() != this) {
      review.setUser(this);
    }
  }

  // OneToMany 연관관계 편의 메서드, 유저 - 리뷰 [양방향]
  public void removeReview(Review review) {
    this.reviews.remove(review);
    if (review.getUser() == this) {
      review.setUser(null);
    }
  }

  // OneToMany 연관관계 편의 메서드, 유저 - 질문 [양방향]
  public void addQuestion(Question question) {
    this.questions.add(question);
    if (question.getUser() != this) {
      question.setUser(this);
    }
  }

  // OneToMany 연관관계 편의 메서드, 유저 - 질문 [양방향]
  public void removeQuestion(Question question) {
    this.questions.remove(question);
    if (question.getUser() == this) {
      question.setUser(null);
    }
  }

  // OneToMany 연관관계 편의 메서드, 유저 - 답변 [양방향]
  public void addAnswer(Answer answer) {
    this.answers.add(answer);
    if (answer.getUser() != this) {
      answer.setUser(this);
    }
  }

  // OneToMany 연관관계 편의 메서드, 유저 - 답변 [양방향]
  public void removeAnswer(Answer answer) {
    this.answers.remove(answer);
    if (answer.getUser() == this) {
      answer.setUser(null);
    }
  }

  // OneToMany 연관관계 편의 메서드, 유저 - 예약 [양방향]
  public void addReservation(Reservation reservation) {
    this.reservations.add(reservation);
    if (reservation.getUser() != this) {
      reservation.setUser(this);
    }
  }

  // OneToMany 연관관계 편의 메서드, 유저 - 예약 [양방향]
  public void removeReservation(Reservation reservation) {
    this.reservations.remove(reservation);
    if (reservation.getUser() == this) {
      reservation.setUser(null);
    }
  }

  public void updateNameAndEmail(String name, String email) {
    this.name = name;
    this.email = email;
  }

  public void setTotpEnable() {
    this.isTotpEnabled = true;
  }

  public void setTotpDisable() {
    this.isTotpEnabled = false;
  }

  public void modifyUser(ReqModifyProfile req) {
    this.name = req.name();
    this.email = req.email();
    this.phoneNumber = req.phoneNumber();
  }

  public void changePassword(String password) {
    this.password = password;
  }

  public void payFeeForHost(int usageFee) {

    if (this.point < usageFee) {
      throw new UserExceededPointBusinessException(ErrorCode.USER_NOT_ENOUGH_POINT);
    }

    this.point -= usageFee;
  }

  public void receivedFee(int usageFee) {

    if (usageFee < 0 && this.getPoint() - usageFee < 0) {
      throw new UserExceededPointBusinessException(ErrorCode.USER_NOT_ENOUGH_POINT);
    }

    this.point += usageFee;
  }

  public static void main(String[] args) {
    User user = new User();
    user.softDelete();
  }
}
