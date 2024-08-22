package com.juny.spacestory.reservation.entity.prices;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "holidays")
public class Holiday {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  LocalDate date;

  @ManyToMany(mappedBy = "subCategories")
  private List<ReservationInfo> reservationInfos = new ArrayList<>();

  public Holiday(LocalDate date) {
    this.date = date;
  }

  // ManyToMany 연관관계 편의 메서드, 휴일 - 예약정보 [양방향]
  public void addReservationInfo(ReservationInfo reservationInfo) {
    if (!this.reservationInfos.contains(reservationInfo)) {
      this.reservationInfos.add(reservationInfo);
      reservationInfo.addHoliday(this);
    }
  }

  // ManyToMany 연관관계 편의 메서드, 휴일 - 예약정보  [양방향]
  public void removeReservationInfo(ReservationInfo reservationInfo) {
    if (this.reservationInfos.remove(reservationInfo)) {
      reservationInfo.removeHoliday(this);
    }
  }
}
