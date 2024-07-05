package com.juny.spacestory.space.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.List;

@Entity
@Table(name = "hashtags")
public class Hashtag {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String name;

  @ManyToMany(mappedBy = "hashtags")
  private List<Space> spaces;

  // 연관관계 편의 메서드
  public void addSpace(Space space) {
    if (!this.spaces.contains(space)) {
      this.spaces.add(space);
      space.addHashtag(this);
    }
  }

  // 연관관계 편의 메서드
  public void removeSpace(Space space) {
    if (this.spaces.contains(space)) {
      this.spaces.remove(space);
      space.removeHashtag(this);
    }
  }
}
