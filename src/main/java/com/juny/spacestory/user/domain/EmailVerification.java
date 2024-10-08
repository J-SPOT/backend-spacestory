package com.juny.spacestory.user.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "email_verifications")
public class EmailVerification {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String email;
  private String code;
  private LocalDateTime expirationDate;

  public EmailVerification(String email, String code, LocalDateTime expirationDate) {
    this.email = email;
    this.code = code;
    this.expirationDate = expirationDate;
  }

  public void resendCode(String code) {

    this.code = code;
    this.expirationDate = LocalDateTime.now().plusMinutes(3L);
  }

  public boolean isExpired() {

    return LocalDateTime.now().isAfter(expirationDate);
  }
}
