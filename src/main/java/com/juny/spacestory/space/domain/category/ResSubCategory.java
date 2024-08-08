package com.juny.spacestory.space.domain.category;

public record ResSubCategory(Long id, String name, Long mainCategoryId, String mainCategoryName) {
  public ResMainCategory mainCategory() {
    return new ResMainCategory(mainCategoryId, mainCategoryName);
  }
}
