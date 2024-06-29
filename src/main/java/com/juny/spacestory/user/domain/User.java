package com.juny.spacestory.user.domain;

import com.juny.spacestory.host.Host;
import com.juny.spacestory.global.exception.ErrorCode;
import com.juny.spacestory.global.exception.hierarchy.user.UserExceededPointBusinessException;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.time.LocalDateTime;
import org.hibernate.annotations.GenericGenerator;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  @Column(columnDefinition = "BINARY(16)")
  private UUID id;

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

  private boolean isTotpEnabled;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "user_ip_addresses", joinColumns = @JoinColumn(name = "user_id"))
  private List<String> ipAddresses = new ArrayList<>();

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

  public void setTotpEnable() {
    this.isTotpEnabled = true;
  }
}
