package com.juny.spacestory.reservation.entity.reservation_type;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

//@Entity
//@AllArgsConstructor
//@NoArgsConstructor
//@Getter
//@Table(name = "day_schedules")
//public class DaySchedule {
//
//  @Id
//  @GeneratedValue(strategy = GenerationType.IDENTITY)
//  private Long id;
//
//  private int year;
//  private int month;
//  private int day;
//  private String dayOfWeek;
//  private String holiday;
//  private boolean available;
//
//
//  @OneToMany(mappedBy = "daySchedule", cascade = CascadeType.ALL, orphanRemoval = true)
//  private List<TimeSlot> timeSlots = new ArrayList<>();
//
//  @OneToMany(mappedBy = "daySchedule", cascade = CascadeType.ALL, orphanRemoval = true)
//  private List<PackageSlot> packageSlots = new ArrayList<>();
//}
