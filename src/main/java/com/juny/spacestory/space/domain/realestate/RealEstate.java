package com.juny.spacestory.space.domain.realestate;

import com.juny.spacestory.host.Host;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RealEstate {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Embedded private Address address;

  @Column(nullable = false)
  private Integer floor;

  @Column(nullable = false)
  private Boolean hasParking;

  @Column(nullable = false)
  private Boolean hasElevator;

  @Column(nullable = false)
  private Boolean isDeleted;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "host_id")
  private Host host;

  public RealEstate(Address address, Integer floor, Boolean hasParking, Boolean hasElevator) {
    this.address = address;
    this.floor = floor;
    this.hasParking = hasParking;
    this.hasElevator = hasElevator;
  }

  // 연관관계 편의 메서드
  public void setHost(Host host) {
    this.host = host;
  }
}
