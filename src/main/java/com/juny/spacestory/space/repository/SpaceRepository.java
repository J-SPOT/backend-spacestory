package com.juny.spacestory.space.repository;

import com.juny.spacestory.space.domain.Space;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpaceRepository extends JpaRepository<Space, Long>, CustomSpaceRepository {

  Page<Space> findAll(Pageable pageable);

  Page<Space> findByRealEstateId(Long id, Pageable pageable);

  Page<Space> findAllByOrderByViewCountDesc(Pageable pageable);

  Page<Space> findAllByOrderByLikeCountDesc(Pageable pageable);

  Page<Space> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
