package com.juny.spacestory.category;

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
@Table(name = "main_categories")
@Getter
@NoArgsConstructor
public class MainCategory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String name;

  @OneToMany(mappedBy = "mainCategory")
  private List<SubCategory> subCategories = new ArrayList<>();

  // OneToMany 연관관계 편의 메서드, 메인카테고리 - 서브카테고리 [양방향]
  public void addSubCategory(SubCategory subCategory) {
    this.subCategories.add(subCategory);
    if (subCategory.getMainCategory() != this) {
      subCategory.setMainCategory(this);
    }
  }

  // OneToMany 연관관계 편의 메서드, 메인카테고리 - 서브카테고리 [양방향]
  public void removeSubCategory(SubCategory subCategory) {
    subCategories.remove(subCategory);
    if (subCategory.getMainCategory() == this) {
      subCategory.setMainCategory(null);
    }
  }

  public MainCategory(String name) {
    this.name = name;
  }

  public void changeCategoryName(String newName) {
    this.name = newName;
  }
}
