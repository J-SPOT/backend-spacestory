package com.juny.spacestory.email.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class EmailVerification {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String email;
  private String code;
  private LocalDateTime expirationDate;
  private boolean isVerified;

  public EmailVerification(String email, String code, LocalDateTime expirationDate) {
    this.email = email;
    this.code = code;
    this.expirationDate = expirationDate;
    this.isVerified = false;
  }

  public void verifyCode() {

    this.isVerified = true;
  }

  public void resendCode(String code) {

    this.code = code;
    this.expirationDate = LocalDateTime.now().plusMinutes(3L);
  }

  public boolean isExpired() {

    return LocalDateTime.now().isAfter(expirationDate);
  }
}
