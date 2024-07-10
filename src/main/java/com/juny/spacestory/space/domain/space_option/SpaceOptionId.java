package com.juny.spacestory.space.domain.space_option;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@Getter
public class SpaceOptionId {

  @Column(name = "space_id")
  private Long spaceId;

  @Column(name = "option_id")
  private Long optionId;

  public SpaceOptionId(Long spaceId, Long optionId) {
    this.spaceId = spaceId;
    this.optionId = optionId;
  }
}
