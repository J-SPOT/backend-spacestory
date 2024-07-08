package com.juny.spacestory.space.domain.realestate;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class Address {
  private String roadAddress;
  private String jibunAddress;
  private String sido;
  private String sigungu;
  private String dong;
}
