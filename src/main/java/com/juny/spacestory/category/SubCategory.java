package com.juny.spacestory.category;

import com.juny.spacestory.detailed_space.DetailedSpace;
import com.juny.spacestory.space.domain.Space;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
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
  private List<Space> spaces = new ArrayList<>();

  @ManyToMany(mappedBy = "subCategories")
  private List<DetailedSpace> detailedSpaces = new ArrayList<>();

  public SubCategory(String name) {
    this.name = name;
  }

  // ManyToOne 연관관계 편의 메서드, 서브카테고리 - 메인카테고리 [양방향]
  public void setMainCategory(MainCategory mainCategory) {
    if (this.mainCategory != null) {
      this.mainCategory.getSubCategories().remove(this);
    }
    this.mainCategory = mainCategory;
    if (mainCategory != null && !mainCategory.getSubCategories().contains(this)) {
      mainCategory.getSubCategories().add(this);
    }
  }

  // ManyToMany 연관관계 편의 메서드, 서브카테고리 - 공간 [양방향]
  public void addSpace(Space space) {
    if (!this.spaces.contains(space)) {
      this.spaces.add(space);
      space.addSubCategory(this);
    }
  }

  // ManyToMany 연관관계 편의 메서드, 서브카테고리 - 공간 [양방향]
  public void removeSpace(Space space) {
    if (this.spaces.remove(space)) {
      space.removeSubCategory(this);
    }
  }

  // ManyToMany 연관관계 편의 메서드, 서브카테고리 - 상세공간 [양방향]
  public void addDetailedSpace(DetailedSpace detailedSpace) {
    if (!this.spaces.contains(detailedSpace)) {
      this.detailedSpaces.add(detailedSpace);
      detailedSpace.addSubCategory(this);
    }
  }

  // ManyToMany 연관관계 편의 메서드, 서브카테고리 - 상세공간 [양방향]
  public void removeDetailedSpace(DetailedSpace detailedSpace) {
    if (this.detailedSpaces.remove(detailedSpace)) {
      detailedSpace.removeSubCategory(this);
    }
  }



  public void changeCategoryName(String newName) {
    this.name = newName;
  }
}
