package com.juny.spacestory.user.domain;

import com.juny.spacestory.host.Host;
import com.juny.spacestory.global.exception.ErrorCode;
import com.juny.spacestory.global.exception.hierarchy.user.UserExceededPointBusinessException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, unique = true)
  private String email;

  @Column private String password;

  private Long point;

  private LocalDateTime deletedAt;

  @Enumerated(EnumType.STRING)
  private Role role;

  private String socialId;

  public User(String name, String email, String password) {
    this.name = name;
    this.email = email;
    this.password = password;
    this.point = 0L;
    this.role = Role.USER;
  }

  public User(String name, String email, Role role, String socialId) {
    this.name = name;
    this.email = email;
    this.point = 0L;
    this.role = role;
    this.socialId = socialId;
  }

  public User(String email, Role role) {
    this.email = email;
    this.role = role;
  }

  public void rechargePoint(Long reqPoint) {
    this.point += reqPoint;
  }

  public void payFee(long usageFee, Host host) {

    if (this.point < usageFee) {
      throw new UserExceededPointBusinessException(ErrorCode.USER_NOT_ENOUGH_POINT);
    }

    this.point -= usageFee;
    host.receivedFee(usageFee);
  }

  public void getRefund(long differenceAmount, Host host) {
    this.point += differenceAmount;
    host.receivedFee(-differenceAmount);
  }

  public void updateNameAndEmail(String name, String email) {
    this.name = name;
    this.email = email;
  }
}
