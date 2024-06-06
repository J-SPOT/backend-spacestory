package com.juny.spacestory.space.repository;

import com.juny.spacestory.space.domain.SpaceType;
import com.juny.spacestory.space.domain.DetailedType;
import com.juny.spacestory.space.domain.Space;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;


public interface SpaceRepository extends JpaRepository<Space, Long>, CustomSpaceRepository {

  Optional<Space> findByIdAndIsDeletedFalse(Long spaceId);

  @Query(
      "select s from Space s where s.spaceType = :spaceType and s.realEstate.address.sido = '서울특별시' and s.isDeleted = false")
  Page<Space> findBySpaceTypeInSeoul(@Param("spaceType") SpaceType spaceType, Pageable pageable);

  @Query(
      "SELECT s FROM Space s "
          + "WHERE s.spaceType = :spaceType "
          + "AND s.realEstate.address.sido = :sido "
          + "AND s.realEstate.address.sigungu = :sigungu "
          + "AND s.maxCapacity >= :minCapacity "
          + "AND (:detailedType IS NULL OR :detailedType MEMBER OF s.detailedTypes) "
          + "AND s.isDeleted = false")
  Page<Space> findByCriteria(
      @Param("spaceType") SpaceType spaceType,
      @Param("sido") String sido,
      @Param("sigungu") String sigungu,
      @Param("minCapacity") int minCapacity,
      @Param("detailedType") DetailedType detailedType,
      Pageable pageable);
}
