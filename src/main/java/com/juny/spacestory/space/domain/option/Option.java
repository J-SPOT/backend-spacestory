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

  // ManyToMany[중간 테이블 O] 연관관계 편의 메서드, 옵션 - 공간 [양방향]
  public void addSpace(Space space) {
    SpaceOption spaceOption = new SpaceOption(space, this);
    spaceOptions.add(spaceOption);
    space.getSpaceOptions().add(spaceOption);
  }

  // ManyToMany[중간 테이블 O] 연관관계 편의 메서드, 옵션 - 공간 [양방향]
  public void removeSpace(Space space) {
    SpaceOption spaceOption = new SpaceOption(space, this);
    
    if (spaceOptions.remove(spaceOption)) {
      space.getSpaceOptions().remove(spaceOption);
      spaceOption.setSpace(null);
      spaceOption.setOption(null);
    }
  }

  public void changeOptionName(String name) {
    this.name = name;
  }
}
