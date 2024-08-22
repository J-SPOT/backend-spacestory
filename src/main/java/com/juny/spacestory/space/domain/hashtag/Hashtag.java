package com.juny.spacestory.space.domain.hashtag;

import com.juny.spacestory.space.domain.Space;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "hashtags")
@Getter
@NoArgsConstructor
public class Hashtag {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String name;

  @ManyToMany(mappedBy = "hashtags")
  private List<Space> spaces = new ArrayList<>();

  public Hashtag(String name) {
    this.name = name;
  }

  // ManyToMany[중간 테이블 X] 연관관계 편의 메서드, 해시태그 - 공간 [양방향]
  public void addSpace(Space space) {
    if (!this.spaces.contains(space)) {
      this.spaces.add(space);
      space.addHashtag(this);
    }
  }

  // ManyToMany[중간 테이블 X] 연관관계 편의 메서드, 해시태그 - 공간 [양방향]
  public void removeSpace(Space space) {
    if (this.spaces.remove(space)) {
      space.removeHashtag(this);
    }
  }
}
