package com.juny.spacestory.space.domain.category;

import static com.juny.spacestory.space.domain.category.QMainCategory.mainCategory;
import static com.juny.spacestory.space.domain.category.QSubCategory.subCategory;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomCategoryRepositoryImpl implements CustomCategoryRepository {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<MainCategory> findAllCategories() {

    return queryFactory.selectFrom(mainCategory)
      .leftJoin(subCategory).on(subCategory.mainCategory.id.eq(mainCategory.id))
      .fetchJoin()
      .orderBy(mainCategory.id.asc(), subCategory.id.asc())
      .fetch();
  }
}
