package com.juny.spacestory.audit;

import jakarta.persistence.Column;
import java.time.LocalDateTime;

public abstract class SoftDeleteBaseTimeEntity extends BaseTimeEntity {

  @Column
  private LocalDateTime deletedAt;

  public void softDelete() {
    this.deletedAt = LocalDateTime.now();
  }
}
