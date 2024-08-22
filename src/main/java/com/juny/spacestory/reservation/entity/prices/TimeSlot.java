package com.juny.spacestory.reservation.entity.prices;

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
@Table(name = "time_slots")
public class TimeSlot {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private Integer hour;

  @Column
  private Integer price;

  @Column
  private Boolean isAvailable;

  public TimeSlot(Integer hour, Integer price, Boolean isAvailable) {
    this.hour = hour;
    this.price = price;
    this.isAvailable = isAvailable;
  }
}
