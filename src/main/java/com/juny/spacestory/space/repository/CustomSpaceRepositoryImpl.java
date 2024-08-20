package com.juny.spacestory.space.repository;

import static com.juny.spacestory.space.domain.QSpace.space;
import static com.juny.spacestory.space.domain.hashtag.QHashtag.hashtag;
import static com.juny.spacestory.space.domain.option.QOption.option;
import static com.juny.spacestory.space.domain.realestate.QRealEstate.realEstate;
import static com.juny.spacestory.space.domain.space_option.QSpaceOption.spaceOption;
import static com.querydsl.core.types.dsl.Expressions.allOf;
import com.juny.spacestory.space.domain.hashtag.ResHashtag;
import com.juny.spacestory.space.dto.ResSummarySpace;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomSpaceRepositoryImpl implements CustomSpaceRepository {

  private final JPAQueryFactory queryFactory;
  private static final Map<String, OrderSpecifier<?>> sortMap = new HashMap<>();

  static {
    sortMap.put("view-desc", space.viewCount.desc());
    sortMap.put("like-desc", space.likeCount.desc());
    sortMap.put("created-desc", space.createdAt.desc());
  }

  @Override
  public Page<ResSummarySpace> findSortedSpacesForMainPage(int page, int size, String sort) {
    long total = queryFactory
      .select(space.count())
      .from(space)
      .fetchOne();

    JPAQuery<Tuple> jpaQuery = queryFactory
      .select(
        space.id,
        space.name,
        space.representImage,
        space.hourlyRate,
        space.maxCapacity,
        space.reviewCount,
        space.likeCount,
        realEstate.address.dong)
      .from(space)
      .join(space.realEstate, realEstate)
      .orderBy(sortMap.get(sort));


    List<Tuple> spaces = jpaQuery
      .offset(page * size)
      .limit(size)
      .fetch();

    List<Long> spaceIds = spaces.stream()
      .map(tuple -> tuple.get(space.id))
      .collect(Collectors.toList());

    List<Tuple> hashtags = queryFactory
      .select(space.id, hashtag.id, hashtag.name)
      .from(space)
      .join(space.hashtags, hashtag)
      .where(space.id.in(spaceIds))
      .fetch();

    Map<Long, List<ResHashtag>> hashtagMap = hashtags.stream()
      .collect(Collectors.groupingBy(
        tuple -> tuple.get(space.id),
        Collectors.mapping(tuple -> new ResHashtag(tuple.get(hashtag.id), tuple.get(hashtag.name)), Collectors.toList())
      ));

    List<ResSummarySpace> spacesWithHashtags = spaces.stream()
      .map(spaceTuple -> new ResSummarySpace(
        spaceTuple.get(space.id),
        spaceTuple.get(space.name),
        spaceTuple.get(space.representImage),
        spaceTuple.get(space.hourlyRate),
        spaceTuple.get(space.maxCapacity),
        spaceTuple.get(space.reviewCount),
        spaceTuple.get(space.likeCount),
        spaceTuple.get(realEstate.address.dong),
        hashtagMap.getOrDefault(spaceTuple.get(space.id), Collections.emptyList())
      ))
      .collect(Collectors.toList());

    return new PageImpl<>(spacesWithHashtags, PageRequest.of(page, size), total);
  }

  @Override
  public Page<ResSummarySpace> searchSpacesByFilter(String query, List<String> sigungu, Integer minCapacity,
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

    JPAQuery<Tuple> jpaQuery = queryFactory
      .select(space.id, space.name, space.representImage, space.hourlyRate, space.maxCapacity, space.reviewCount, space.likeCount, realEstate.address.dong)
      .from(space)
      .join(space.realEstate, realEstate)
      .where(predicate);

    jpaQuery = applySorting(jpaQuery, sort);

    List<Tuple> spaces = jpaQuery
      .offset(page * size)
      .limit(size)
      .fetch();

    List<Long> spaceIds = spaces.stream()
      .map(tuple -> tuple.get(space.id))
      .collect(Collectors.toList());

    List<Tuple> hashtags = queryFactory
      .select(space.id, hashtag.id, hashtag.name)
      .from(space)
      .join(space.hashtags, hashtag)
      .where(space.id.in(spaceIds))
      .fetch();

    Map<Long, List<ResHashtag>> hashtagMap = hashtags.stream()
      .collect(Collectors.groupingBy(
        tuple -> tuple.get(space.id),
        Collectors.mapping(tuple -> new ResHashtag(tuple.get(hashtag.id), tuple.get(hashtag.name)), Collectors.toList())
      ));

    List<ResSummarySpace> spacesWithHashtags = spaces.stream()
      .map(spaceTuple -> new ResSummarySpace(
        spaceTuple.get(space.id),
        spaceTuple.get(space.name),
        spaceTuple.get(space.representImage),
        spaceTuple.get(space.hourlyRate),
        spaceTuple.get(space.maxCapacity),
        spaceTuple.get(space.reviewCount),
        spaceTuple.get(space.likeCount),
        spaceTuple.get(realEstate.address.dong),
        hashtagMap.getOrDefault(spaceTuple.get(space.id), Collections.emptyList())
      ))
      .collect(Collectors.toList());

    return new PageImpl<>(spacesWithHashtags, PageRequest.of(page, size), total);
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


  private JPAQuery<Tuple> applySorting(JPAQuery<Tuple> query, String sort) {

    switch (sort) {
      case "price-asc":
        return query.orderBy(space.hourlyRate.asc());
      case "price-desc":
        return query.orderBy(space.hourlyRate.desc());
      case "review-desc":
        return query.orderBy(space.reviewCount.desc());
      case "like-desc":
        return query.orderBy(space.likeCount.desc());
      default:
        return query.orderBy(space.viewCount.desc());
    }
  }
}
