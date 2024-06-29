package com.juny.spacestory.user.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "totp_verifications")
@Getter
@NoArgsConstructor
public class TotpVerification {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String email;
  private String secret;

  public TotpVerification(String email, String secret) {
    this.email = email;
    this.secret = secret;
  }
}
