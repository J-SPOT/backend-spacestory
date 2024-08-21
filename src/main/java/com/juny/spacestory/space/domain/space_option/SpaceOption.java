package com.juny.spacestory.space.domain.space_option;

import com.juny.spacestory.space.domain.Space;
import com.juny.spacestory.space.domain.option.Option;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "detailed_space_options")
@Getter
@NoArgsConstructor
public class SpaceOption {

  @EmbeddedId
  private SpaceOptionId id;

  @ManyToOne
  @JoinColumn(name = "space_id", insertable = false, updatable = false)
  private Space space;

  @ManyToOne
  @JoinColumn(name = "option_id", insertable = false, updatable = false)
  private Option option;

  public SpaceOption(Space space, Option option) {
    this.space = space;
    this.option = option;
    this.id = new SpaceOptionId(space.getId(), option.getId());
  }

  public void setSpace(Space space) {
    this.space = space;
  }

  public void setOption(Option option) {
    this.option = option;
  }
}
