package com.juny.spacestory.space.repository;

import com.juny.spacestory.space.domain.MainCategory;
import java.util.List;

public interface CustomCategoryRepository {
  List<MainCategory> findAllCategories();
}
