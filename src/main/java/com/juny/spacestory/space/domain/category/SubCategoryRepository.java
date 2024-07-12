package com.juny.spacestory.space.domain.category;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {

  List<SubCategory> findByMainCategory_Id(Long mainCategoryId);

  Optional<SubCategory> findByName(String name);
}
