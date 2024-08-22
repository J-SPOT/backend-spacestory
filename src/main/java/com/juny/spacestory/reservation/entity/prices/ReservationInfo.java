package com.juny.spacestory.reservation.entity.prices;

import com.juny.spacestory.detailed_space.DetailedSpace;
import com.juny.spacestory.detailed_space.ReservationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "reservation_infos")
public class ReservationInfo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  @Enumerated(EnumType.STRING)
  private ReservationType reservationType;

  @Column
  private Integer baseRate;

  @Column
  private boolean chargingPerPerson;

  @Column
  private Integer standardOccupancy;

  @Column
  private Integer extraPricePerPersonForPackage;

  @Column
  private Integer extraPricePerPersonForTime;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "detailed_space_id")
  private DetailedSpace detailedSpace;

  @OneToMany
  private List<Price> prices = new ArrayList<>();

  @OneToMany
  private List<DaySchedule> daySchedules = new ArrayList<>();

  @ManyToMany
  @JoinTable(
    name = "reservation_type_holidays",
    joinColumns = @JoinColumn(name = "reservation_info_id"),
    inverseJoinColumns = @JoinColumn(name = "holiday_id")
  )
  private List<Holiday> holidays = new ArrayList<>();

  public ReservationInfo(ReservationType reservationType, Integer baseRate,
    boolean chargingPerPerson,
    Integer standardOccupancy, Integer extraPricePerPersonForPackage,
    Integer extraPricePerPersonForTime, DetailedSpace detailedSpace) {
    this.reservationType = reservationType;
    this.baseRate = baseRate;
    this.chargingPerPerson = chargingPerPerson;
    this.standardOccupancy = standardOccupancy;
    this.extraPricePerPersonForPackage = extraPricePerPersonForPackage;
    this.extraPricePerPersonForTime = extraPricePerPersonForTime;
    this.detailedSpace = detailedSpace;
  }

  // ManyToOne 연관관계 편의 메서드, 예약정보 - 상세공간 [양방향]
  public void setDetailedSpace(DetailedSpace detailedSpace) {
    if (this.detailedSpace != null) {
      this.detailedSpace.getReservationInfos().remove(this);
    }
    this.detailedSpace = detailedSpace;
    if (detailedSpace != null && !detailedSpace.getReservationInfos().contains(this)) {
      detailedSpace.getReservationInfos().add(this);
    }
  }

  // OneToMany 연관관계 편의 메서드, 예약정보 - 가격 [단방향]
  public void addPrice(Price price) {
    prices.add(price);
  }

  // OneToMany 연관관계 편의 메서드, 예약정보 - 가격 [단방향]
  public void removePrice(Price price) {
    prices.remove(price);
  }

  // OneToMany 연관관계 편의 메서드, 예약정보 - 일스케줄 [단방향]
  public void addDaySchedule(DaySchedule daySchedule) {
    daySchedules.add(daySchedule);
  }

  // OneToMany 연관관계 편의 메서드, 예약정보 - 일스케줄 [단방향]
  public void removeDaySchedule(DaySchedule daySchedule) {
    daySchedules.remove(daySchedule);
  }

  // ManyToMany[중간 테이블 X] 연관관계 편의 메서드, 예약정보 - 휴일 [양방향]
  public void addHoliday(Holiday holiday) {
    if (!this.holidays.contains(holiday)) {
      this.holidays.add(holiday);
      holiday.addReservationInfo(this);
    }
  }

  // ManyToMany[중간 테이블 X] 연관관계 편의 메서드, 예약정보 - 휴일 [양방향]
  public void removeHoliday(Holiday holiday) {
    if (this.holidays.remove(holiday)) {
      holiday.removeReservationInfo(this);
    }
  }
}
