package com.juny.spacestory.reservation.entity.prices;

import com.juny.spacestory.space.domain.dayoff.DayOfWeekWithHoliday;
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
@Table(name = "day_prices")
public class DayPrice {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private DayOfWeekWithHoliday dayOfWeekWithHoliday;

  @Column
  private Integer price;

  @Column
  private Integer startHour;

  public DayPrice(DayOfWeekWithHoliday dayOfWeekWithHoliday, Integer price, Integer startHour) {
    this.dayOfWeekWithHoliday = dayOfWeekWithHoliday;
    this.price = price;
    this.startHour = startHour;
  }
}
