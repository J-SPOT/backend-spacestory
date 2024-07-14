package com.juny.spacestory.user.domain;

import com.juny.spacestory.global.exception.ErrorCode;
import com.juny.spacestory.global.exception.hierarchy.user.UserExceededPointBusinessException;
import com.juny.spacestory.reservation.entity.Reservation;
import com.juny.spacestory.user.dto.ReqModifyProfile;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(columnDefinition = "BINARY(16)")
  private UUID id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, unique = true)
  private String email;

  private String phoneNumber;

  @Column private String password;

  private Long point;

  private LocalDateTime deletedAt;

  @Enumerated(EnumType.STRING)
  private Role role;

  private String socialId;

  private Boolean isTotpEnabled;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "user_ip_addresses", joinColumns = @JoinColumn(name = "user_id"))
  private List<String> ipAddresses = new ArrayList<>();

  @OneToMany(mappedBy = "user")
  private List<Reservation> reservations = new ArrayList<>();

  // 일반 로그인
  public User(String name, String email, String password, String ip) {
    this.name = name;
    this.email = email;
    this.password = password;
    this.point = 0L;
    this.role = Role.USER;
    this.ipAddresses.add(ip);
    this.isTotpEnabled = false;
  }

  // 소셜 로그인
  public User(String name, String email, Role role, String socialId) {
    this.name = name;
    this.email = email;
    this.point = 0L;
    this.role = role;
    this.socialId = socialId;
    this.isTotpEnabled = false;
  }

  // jwt토큰으로 SecurityContextHolder에 저장
  public User(UUID id, Role role) {
    this.id = id;
    this.role = role;
  }

  // 연관관계 편의 메서드
  public void addReservation(Reservation reservation) {
    this.reservations.add(reservation);
    if (reservation.getUser() != this) {
      reservation.setUser(this);
    }
  }

  // 연관관계 편의 메서드
  public void removeReservation(Reservation reservation) {
    this.reservations.remove(reservation);
    if (reservation.getUser() == this) {
      reservation.setUser(null);
    }
  }

  public void updateNameAndEmail(String name, String email) {
    this.name = name;
    this.email = email;
  }

  public void setTotpEnable() {
    this.isTotpEnabled = true;
  }

  public void setTotpDisable() {
    this.isTotpEnabled = false;
  }

  public void modifyUser(ReqModifyProfile req) {
    this.name = req.name();
    this.email = req.email();
    this.phoneNumber = req.phoneNumber();
  }

  public void changePassword(String password) {
    this.password = password;
  }

  public void payFeeForHost(long usageFee) {

    if (this.point < usageFee) {
      throw new UserExceededPointBusinessException(ErrorCode.USER_NOT_ENOUGH_POINT);
    }

    this.point -= usageFee;
  }

  public void receivedFee(long usageFee) {

    if (usageFee < 0 && this.getPoint() - usageFee < 0) {
      throw new UserExceededPointBusinessException(ErrorCode.USER_NOT_ENOUGH_POINT);
    }

    this.point += usageFee;
  }

  public void softDelete() {
    this.deletedAt = LocalDateTime.now();
  }
}
