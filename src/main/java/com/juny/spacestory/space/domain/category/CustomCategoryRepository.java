package com.juny.spacestory.space.domain.category;

import java.util.List;

public interface CustomCategoryRepository {
  List<MainCategory> findAllCategories();
}
