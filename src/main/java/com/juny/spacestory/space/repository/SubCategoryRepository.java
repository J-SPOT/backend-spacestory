package com.juny.spacestory.space.repository;

import com.juny.spacestory.space.domain.SubCategory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {

  List<SubCategory> findByMainCategory_Id(Long mainCategoryId);
}
