package com.juny.spacestory.global.security.jwt.refresh;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "refreshes")
@NoArgsConstructor
public class Refresh {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private UUID userId;

  @Column(nullable = false)
  private String refresh;

  @Column(nullable = false)
  private String expiration;

  public Refresh(UUID id, String refresh, String expiration) {
    this.userId = id;
    this.refresh = refresh;
    this.expiration = expiration;
  }
}
