package com.juny.spacestory.space.domain.realestate;

import com.juny.spacestory.user.domain.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "real_estates")
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

  private LocalDateTime deletedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  public RealEstate(Address address, Integer floor, Boolean hasParking, Boolean hasElevator) {
    this.address = address;
    this.floor = floor;
    this.hasParking = hasParking;
    this.hasElevator = hasElevator;
  }

  // 연관관계 편의 메서드
  public void setHost(User user) {
    this.user = user;
  }

  public void updateRealEstateForm(ReqRealEstate req) {
    this.address = req.address();
    this.floor = req.floor();
    this.hasParking = req.hasParking();
    this.hasElevator = req.hasElevator();
  }
}
