package com.juny.spacestory.reservation.entity.prices;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "prices")
public class Price {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private LocalDate startDate;

  @Column
  private LocalDate endDate;

  @Column
  private boolean isSpecificPrice;

  @Column
  private String title;

  @Column
  private int startHour;

  @Column
  private int endHour;

  @OneToMany
  private List<DayPrice> dayPrices = new ArrayList<>();

  public Price(LocalDate startDate, LocalDate endDate, boolean isSpecificPrice, String title,
    int startHour, int endHour) {
    this.startDate = startDate;
    this.endDate = endDate;
    this.isSpecificPrice = isSpecificPrice;
    this.title = title;
    this.startHour = startHour;
    this.endHour = endHour;
  }

  // OneToMany 연관관계 편의 메서드, 가격 - 일가격 [단방향]
  public void addDayPrice(DayPrice dayPrice) {
    dayPrices.add(dayPrice);
  }
}
