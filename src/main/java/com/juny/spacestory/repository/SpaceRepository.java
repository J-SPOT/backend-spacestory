package com.juny.spacestory.repository;

import com.juny.spacestory.domain.DetailedType;
import com.juny.spacestory.domain.Space;
import com.juny.spacestory.domain.SpaceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SpaceRepository extends JpaRepository<Space, Long>{

    Optional<Space> findById(Long spaceId);

    @Query("select s from Space s where s.spaceType = :spaceType and s.realEstate.address.sido = '서울특별시'")
    Page<Space> findBySpaceTypeAndRealEstateAddressSido(@Param("spaceType") SpaceType spaceType, Pageable pageable);

    @Query("SELECT s FROM Space s " +
            "WHERE s.spaceType = :spaceType " +
            "AND s.realEstate.address.sido = :sido " +
            "AND s.realEstate.address.sigungu = :sigungu " +
            "AND s.maxCapacity >= :minCapacity " +
            "AND (:detailedType IS NULL OR :detailedType MEMBER OF s.detailedTypes)")
    Page<Space> findByCriteria(@Param("spaceType") SpaceType spaceType,
                               @Param("sido") String sido,
                               @Param("sigungu") String sigungu,
                               @Param("minCapacity") int minCapacity,
                               @Param("detailedType") DetailedType detailedType,
                               Pageable pageable);
}
