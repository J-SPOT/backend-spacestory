package com.juny.spacestory.space.domain.option;

import com.juny.spacestory.space.domain.Space;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
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

  @ManyToMany(mappedBy = "options")
  private List<Space> spaces;

  public Option(String name) {
    this.name = name;
  }

  // 연관관계 편의 메서드
  public void addSpace(Space space) {
    if (!this.spaces.contains(space)) {
      this.spaces.add(space);
      space.addOption(this);
    }
  }

  // 연관관계 편의 메서드
  public void removeSpace(Space space) {
    if (this.spaces.contains(space)) {
      this.spaces.remove(space);
      space.removeOption(this);
    }
  }

  public void changeOptionName(String name) {
    this.name = name;
  }
}
