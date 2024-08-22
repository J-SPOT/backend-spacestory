package com.juny.spacestory.space.domain.space_option;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SpaceOptionId that = (SpaceOptionId) o;
    return Objects.equals(spaceId, that.spaceId) &&
      Objects.equals(optionId, that.optionId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(spaceId, optionId);
  }

  public SpaceOptionId(Long spaceId, Long optionId) {
    this.spaceId = spaceId;
    this.optionId = optionId;
  }
}
