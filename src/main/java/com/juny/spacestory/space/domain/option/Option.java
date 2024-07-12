package com.juny.spacestory.space.domain.option;

import com.juny.spacestory.space.domain.Space;
import com.juny.spacestory.space.domain.space_option.SpaceOption;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "options")
@NoArgsConstructor
@Getter
public class Option {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String name;

  @OneToMany(mappedBy = "option", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<SpaceOption> spaceOptions = new ArrayList<>();

  public Option(String name) {
    this.name = name;
  }

  // 연관관계 편의 메서드
  public void addSpace(Space space) {
    SpaceOption spaceOption = new SpaceOption(space, this);
    spaceOptions.add(spaceOption);
    space.getSpaceOptions().add(spaceOption);
  }

  // 연관관계 편의 메서드
  public void removeSpace(Space space) {
    for (var spaceOption : new ArrayList<>(spaceOptions)) {
      if (spaceOption.getSpace().equals(space) && spaceOption.getOption().equals(this)) {
        spaceOptions.remove(spaceOption);
        space.getSpaceOptions().remove(spaceOption);
        spaceOption.setSpace(null);
        spaceOption.setOption(null);
      }
    }
  }

  public void changeOptionName(String name) {
    this.name = name;
  }
}
