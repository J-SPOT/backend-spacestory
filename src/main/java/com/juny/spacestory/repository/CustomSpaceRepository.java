package com.juny.spacestory.repository;

import com.juny.spacestory.domain.DetailedType;
import com.juny.spacestory.domain.Space;
import com.juny.spacestory.domain.SpaceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface CustomSpaceRepository {
    Page<Space> findBySpaceTypeInSeoulQuerydsl(SpaceType spaceType, Pageable pageable);

    Page<Space> findByCriteriaQuerydsl(SpaceType spaceType, String sido, String sigungu, int minCapacity, Set<DetailedType> detailedTypes, Pageable pageable);
}
