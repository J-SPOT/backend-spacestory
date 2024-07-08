package com.juny.spacestory.reservation.entity;

import com.juny.spacestory.host.Host;
import com.juny.spacestory.reservation.dto.RequestUpdateReservation;
import com.juny.spacestory.space.domain.Space;
import com.juny.spacestory.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "reservations")
public class Reservation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private LocalDate reservationDate;

  @Column(nullable = false)
  private LocalTime startTime;

  @Column(nullable = false)
  private LocalTime endTime;

  @Column(nullable = false)
  private Long fee;

  @Column(nullable = false)
  private LocalDateTime deletedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "space_id")
  private Space space;

  public Reservation(LocalDate reservationDate, LocalTime startTime, LocalTime endTime, Long fee) {
    this.reservationDate = reservationDate;
    this.startTime = startTime;
    this.endTime = endTime;
    this.fee = fee;
  }

  // 연관관계 편의 메서드
  public void setUser(User user) {
    if (this.user != null) {
      this.user.getReservations().remove(this);
    }
    this.user = user;
    if (user != null && !user.getReservations().contains(this)) {
      user.getReservations().add(this);
    }
  }

  public void updateReservation(RequestUpdateReservation req, User user, Host host) {
    this.reservationDate = req.reservationDate();
    this.startTime = req.startTime();
    this.endTime = req.endTime();
    long differenceAmount =
      getFee() - Duration.between(startTime, endTime).toHours() * this.space.getHourlyRate();
    if (req.isUser()) {
      if (differenceAmount < 0) {
        user.payFee(-differenceAmount, host);
      }
      if (differenceAmount > 0) {
        user.getRefund(differenceAmount, host);
      }
    }
    this.fee -= differenceAmount;
  }

  public void softDelete(Reservation reservation) {

    reservation.deletedAt = LocalDateTime.now();
  }
}
