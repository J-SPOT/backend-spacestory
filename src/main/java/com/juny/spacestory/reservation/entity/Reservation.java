package com.juny.spacestory.reservation.entity;

import com.juny.spacestory.detailed_space.DetailedSpace;
import com.juny.spacestory.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "reservations")
public class Reservation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  @Enumerated(EnumType.STRING)
  private ReservationStatus status;

  @Column(nullable = false)
  private LocalDateTime startTime;

  @Column(nullable = false)
  private LocalDateTime endTime;

  @Column(nullable = false)
  private Integer fee;

  @Column
  private Integer numberOfGuests;

  @Column
  private Long originReservationId;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @Column
  private LocalDateTime deletedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "detailed_space_id")
  private DetailedSpace detailedSpace;

//  @OneToOne(fetch = FetchType.LAZY)
//  @JoinColumn(name = "reservation_type_id", nullable = false)
//  private ReservationType reservationType;

  public Reservation(LocalDate reservationDate, LocalTime startTime, LocalTime endTime, Long fee) {
//    this.reservationDate = reservationDate;
//    this.startTime = startTime;
//    this.endTime = endTime;
//    this.fee = fee;
    this.createdAt = LocalDateTime.now();
    this.status = ReservationStatus.대기;
  }

  public Reservation(LocalDate reservationDate, LocalTime startTime, LocalTime endTime, Long fee, Long originReservationId) {
//    this.reservationDate = reservationDate;
//    this.startTime = startTime;
//    this.endTime = endTime;
//    this.fee = fee;
    this.createdAt = LocalDateTime.now();
    this.status = ReservationStatus.대기;
    this.originReservationId = originReservationId;
  }

  // ManyToOne 연관관계 편의 메서드, 예약 - 상세공간 [단방향]
  public void setDetailSpaces(DetailedSpace detailSpaces) {
    this.detailedSpace = detailSpaces;
  }

  // ManyToOne 연관관계 편의 메서드, 예약 - 유저 [양방향]
  public void setUser(User user) {
    if (this.user != null) {
      this.user.getReservations().remove(this);
    }
    this.user = user;
    if (user != null && !user.getReservations().contains(this)) {
      user.getReservations().add(this);
    }
  }

  public void approveReservation() {

    this.status = ReservationStatus.승인;
  }

  public void cancelReservation() {

    this.status = ReservationStatus.취소;
  }

  public void softDelete() {

    this.deletedAt = LocalDateTime.now();
  }
}
