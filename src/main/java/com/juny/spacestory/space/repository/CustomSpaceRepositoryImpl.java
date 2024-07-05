package com.juny.spacestory.space.repository;

import static com.juny.spacestory.space.domain.QSpace.space;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

//@Repository
//@RequiredArgsConstructor
//public class CustomSpaceRepositoryImpl implements CustomSpaceRepository {
//
//  private final JPAQueryFactory jpaQueryFactory;
//
//  @Override
//  public Page<Space> findBySpaceTypeInSeoulQuerydsl(SpaceType spaceType, Pageable pageable) {
//    return null;
//  }
//
//  @Override
//  public Page<Space> findByCriteriaQuerydsl(SpaceType spaceType, String sido, String sigungu,
//    int minCapacity, Set<DetailedType> detailedTypes, Pageable pageable) {
//    return null;
//  }
//
//  @Override
//  public Page<Space> findBySpaceTypeInSeoulQuerydsl(SpaceType spaceType, Pageable pageable) {
//    List<Space> spaces =
//        jpaQueryFactory
//            .selectFrom(space)
//            .where(
//                space
//                    .spaceType
//                    .eq(spaceType)
//                    .and(space.realEstate.address.sido.eq("서울특별시"))
//                    .and(space.isDeleted.isFalse()))
//            .offset(pageable.getOffset())
//            .limit(pageable.getPageSize())
//            .fetch();
//
//    Long total =
//        jpaQueryFactory
//            .select(space.count())
//            .from(space)
//            .where(
//                space
//                    .spaceType
//                    .eq(spaceType)
//                    .and(space.realEstate.address.sido.eq("서울특별시"))
//                    .and(space.isDeleted.isFalse()))
//            .fetchOne();
//    long totalCount = total != null ? total : 0L;
//
//    return new PageImpl<>(spaces, pageable, totalCount);
//  }
//
//  @Override
//  public Page<Space> findByCriteriaQuerydsl(
//      SpaceType spaceType,
//      String sido,
//      String sigungu,
//      int minCapacity,
//      Set<DetailedType> detailedTypes,
//      Pageable pageable) {
//    BooleanExpression conditions =
//        space
//            .spaceType
//            .eq(spaceType)
//            .and(space.realEstate.address.sido.eq(sido))
//            .and(space.realEstate.address.sigungu.eq(sigungu))
//            .and(space.maxCapacity.goe(minCapacity))
//            .and(space.isDeleted.isFalse());
//
//    if (detailedTypes != null && !detailedTypes.isEmpty()) {
//      conditions = conditions.and(space.detailedTypes.any().in(detailedTypes));
//    }
//
//    List<Space> spaces =
//        jpaQueryFactory
//            .selectFrom(space)
//            .where(conditions)
//            .offset(pageable.getOffset())
//            .limit(pageable.getPageSize())
//            .fetch();
//
//    Long total = jpaQueryFactory.select(space.count()).from(space).where(conditions).fetchOne();
//    long totalCount = total != null ? total : 0;
//
//    return new PageImpl<>(spaces, pageable, totalCount);
//  }
//}
