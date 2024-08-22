package com.juny.spacestory.space.domain.dayoff;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "regular_day_offs")
public class RegularDayOff {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private DayOffType dayOffType;

  @Column
  private DayOfWeekWithHoliday dayOfWeek;

  public RegularDayOff(DayOfWeekWithHoliday dayOfWeek) {
    this.dayOfWeek = dayOfWeek;
  }
}
