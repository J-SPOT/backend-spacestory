package com.juny.spacestory.space.domain.category;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MainCategoryRepository extends JpaRepository<MainCategory, Long>,
  CustomCategoryRepository {

}
