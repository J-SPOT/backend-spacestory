package com.juny.spacestory.space.repository;

import static com.juny.spacestory.space.domain.QSpace.space;
import static com.juny.spacestory.space.domain.option.QOption.option;
import static com.juny.spacestory.space.domain.space_option.QSpaceOption.spaceOption;
import static com.querydsl.core.types.dsl.Expressions.allOf;
import com.juny.spacestory.space.domain.Space;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomSpaceRepositoryImpl implements CustomSpaceRepository {

  private final JPAQueryFactory queryFactory;

  @Override
  public Page<Space> searchSpacesByFilter(String query, List<String> sigungu, Integer minCapacity,
    Integer minPrice, Integer maxPrice, List<String> options, String sort, int page, int size) {

    BooleanExpression predicate = allOf(
      containsQuery(query),
      inDistricts(sigungu),
      goeCapacity(minCapacity),
      goePrice(minPrice),
      loePrice(maxPrice),
      containsOption(options)
    );

    long total = queryFactory
      .select(space.count())
      .from(space)
      .where(predicate)
      .fetchOne();

    JPAQuery<Space> jpaQuery = queryFactory.selectFrom(space)
      .where(predicate);

    jpaQuery = applySorting(jpaQuery, sort);

    int offset = page * size;

    List<Space> spaces = jpaQuery
      .offset(offset)
      .limit(size)
      .fetch();

    return new PageImpl<>(spaces, PageRequest.of(page, size), total);
  }

  private BooleanExpression containsQuery(String query) {
    if (query != null && !query.isEmpty()) {
      return space.name.containsIgnoreCase(query)
        .or(space.subCategories.any().name.containsIgnoreCase(query));
    }
    return null;
  }

  private BooleanExpression inDistricts(List<String> sigungu) {

    if (sigungu != null && !sigungu.isEmpty()) {
      return space.realEstate.address.sigungu.in(sigungu);
    }
    return null;
  }

  private BooleanExpression goeCapacity(Integer capacityMin) {

    if (capacityMin != null) {
      return space.maxCapacity.goe(capacityMin);
    }
    return null;
  }

  private BooleanExpression goePrice(Integer priceMin) {

    if (priceMin != null) {
      return space.hourlyRate.goe(priceMin);
    }
    return null;
  }

  private BooleanExpression loePrice(Integer priceMax) {

    if (priceMax != null) {
      return space.hourlyRate.loe(priceMax);
    }
    return null;
  }

  private BooleanExpression containsOption(List<String> options) {

    if (options != null && !options.isEmpty()) {

      return space.id.in(
        JPAExpressions.select(spaceOption.space.id)
          .from(spaceOption)
          .join(spaceOption.option, option)
          .where(option.name.in(options))
      );
    }
    return null;
  }


  private JPAQuery<Space> applySorting(JPAQuery<Space> query, String sort) {

    switch (sort) {
      case "price_asc":
        return query.orderBy(space.hourlyRate.asc());
      case "price_desc":
        return query.orderBy(space.hourlyRate.desc());
      case "review_desc":
        return query.orderBy(space.reviewCount.desc());
      case "like_desc":
        return query.orderBy(space.likeCount.desc());
      default:
        return query.orderBy(space.viewCount.desc());
    }
  }
}
