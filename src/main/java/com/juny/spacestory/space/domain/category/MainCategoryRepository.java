package com.juny.spacestory.space.domain.category;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MainCategoryRepository extends JpaRepository<MainCategory, Long>,
  CustomCategoryRepository {

  Optional<MainCategory> findByName(String name);
}
