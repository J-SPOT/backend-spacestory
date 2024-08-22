package com.juny.spacestory.reservation.entity.prices;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "package_slots")
public class PackageSlot {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private String name;

  @Column
  private Integer startHour;

  @Column
  private Integer endHour;

  @Column
  private Integer price;

  @Column
  private LocalDate startDate;

  @Column
  private LocalDate endDate;

  @Column
  private Boolean available;

  public PackageSlot(String name, Integer startHour, Integer endHour, Integer price,
    LocalDate startDate, LocalDate endDate, Boolean available) {
    this.name = name;
    this.startHour = startHour;
    this.endHour = endHour;
    this.price = price;
    this.startDate = startDate;
    this.endDate = endDate;
    this.available = available;
  }
}
