package com.juny.spacestory.reservation.entity.prices;

import com.juny.spacestory.space.domain.dayoff.DayOfWeekWithHoliday;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "day_schedules")
public class DaySchedule {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private Integer year;

  @Column
  private Integer month;

  @Column
  private Integer day;

  @Column
  private DayOfWeekWithHoliday dayOfWeek;

  @Column
  private Boolean holiday;

  @Column
  private Boolean available;

  @OneToMany
  private List<TimeSlot> timeslots = new ArrayList<>();

  @OneToMany
  private List<PackageSlot> packageSlots = new ArrayList<>();

  public DaySchedule(Integer year, Integer month, Integer day, DayOfWeekWithHoliday dayOfWeek,
    Boolean holiday, Boolean available) {
    this.year = year;
    this.month = month;
    this.day = day;
    this.dayOfWeek = dayOfWeek;
    this.holiday = holiday;
    this.available = available;
  }

  // OneToMany 연관관계 편의 메서드, 일스케줄 - 타임슬롯 [단방향]
  public void addTimeSlot(TimeSlot timeSlot) {
    timeslots.add(timeSlot);
  }

  // OneToMany 연관관계 편의 메서드, 일스케줄 - 패키지슬롯 [단방향]
  public void addTimeSlot(PackageSlot packageSlot) {
    packageSlots.add(packageSlot);
  }
}
