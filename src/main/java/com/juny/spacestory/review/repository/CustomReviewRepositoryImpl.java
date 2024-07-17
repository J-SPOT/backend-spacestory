package com.juny.spacestory.review.repository;

import static com.juny.spacestory.reservation.entity.QReservation.reservation;
import static com.juny.spacestory.review.domain.QReview.review;
import static com.juny.spacestory.space.domain.QSpace.space;

import com.juny.spacestory.review.domain.Review;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomReviewRepositoryImpl implements CustomReviewRepository {

  private final JPAQueryFactory queryFactory;

  @Override
  public Page<Review> findReviewsBySpaceId(Long spaceId, Pageable pageable) {

    long total = queryFactory
      .select(review.count())
      .from(review)
      .join(review.reservation, reservation)
      .join(reservation.space, space)
      .where(reservation.space.id.eq(spaceId)
        .and(review.deletedAt.isNull()))
      .fetchOne();

    JPAQuery<Review> query = queryFactory
      .selectFrom(review)
      .join(review.reservation, reservation).fetchJoin()
      .join(reservation.space, space).fetchJoin()
      .where(reservation.space.id.eq(spaceId)
        .and(review.deletedAt.isNull()));

    List<Review> reviews = query
      .offset(pageable.getOffset())
      .limit(pageable.getPageSize())
      .orderBy(review.id.asc())
      .fetch();

    return new PageImpl<>(reviews, pageable, total);
  }
}
