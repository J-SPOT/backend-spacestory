package com.juny.spacestory.host;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Table(name = "hosts")
public class Host {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String userName;

  @Column(nullable = false)
  private Long point;

  @Column(nullable = false)
  private LocalDateTime deletedAt;

  public Host(String userName, Long point, LocalDateTime deletedAt) {
    this.userName = userName;
    this.point = point;
    this.deletedAt = deletedAt;
  }

  public void receivedFee(long usageFee) {
    point += usageFee;
  }
}
