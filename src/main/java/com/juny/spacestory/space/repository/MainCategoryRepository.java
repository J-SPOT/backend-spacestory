package com.juny.spacestory.space.repository;

import com.juny.spacestory.space.domain.MainCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MainCategoryRepository extends JpaRepository<MainCategory, Long>, CustomCategoryRepository {

}
