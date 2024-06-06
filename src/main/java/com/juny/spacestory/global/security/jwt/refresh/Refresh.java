package com.juny.spacestory.global.security.jwt.refresh;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "refreshes")
@NoArgsConstructor
@AllArgsConstructor
public class Refresh {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String email; // refresh token owner

  @Column(nullable = false)
  private String refresh;

  @Column(nullable = false)
  private String expiration;

  public Refresh(String email, String refresh, String expiration) {
    this.email = email;
    this.refresh = refresh;
    this.expiration = expiration;
  }
}
