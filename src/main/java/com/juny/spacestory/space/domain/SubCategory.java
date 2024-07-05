package com.juny.spacestory.space.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sub_categories")
@NoArgsConstructor
@Getter
public class SubCategory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String name;

  @ManyToOne
  @JoinColumn(name = "main_category_id", nullable = false)
  private MainCategory mainCategory;

  @ManyToMany(mappedBy = "subCategories")
  private List<Space> spaces;

  public SubCategory(String name) {
    this.name = name;
  }

  // 연관관계 편의 메서드
  public void addSpace(Space space) {
    if (!this.spaces.contains(space)) {
      this.spaces.add(space);
      space.addSubCategory(this);
    }
  }

  // 연관관계 편의 메서드
  public void removeSpace(Space space) {
    if (this.spaces.contains(space)) {
      this.spaces.remove(space);
      space.removeSubCategory(this);
    }
  }

  // 연관관계 편의 메서드
  public void setMainCategory(MainCategory mainCategory) {
    this.mainCategory = mainCategory;
  }

  public void changeCategoryName(String newName) {
    this.name = newName;
  }
}
