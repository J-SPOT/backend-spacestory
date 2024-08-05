package com.juny.spacestory.space.repository;

import com.juny.spacestory.space.domain.Space;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpaceRepository extends JpaRepository<Space, Long>, CustomSpaceRepository {

  @EntityGraph(value = "Space.withRealEstate", type = EntityGraph.EntityGraphType.LOAD)
  Page<Space> findAll(Pageable pageable);

  Page<Space> findByRealEstateId(Long id, Pageable pageable);

  @EntityGraph(value = "Space.withRealEstate", type = EntityGraph.EntityGraphType.LOAD)
  Page<Space> findAllByOrderByViewCountDesc(Pageable pageable);

  @EntityGraph(value = "Space.withRealEstate", type = EntityGraph.EntityGraphType.LOAD)
  Page<Space> findAllByOrderByLikeCountDesc(Pageable pageable);

  @EntityGraph(value = "Space.withRealEstate", type = EntityGraph.EntityGraphType.LOAD)
  Page<Space> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
