package com.juny.spacestory.repository;

import com.juny.spacestory.domain.RealEstate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RealEstateRepository extends JpaRepository<RealEstate, Long> {
    Optional<RealEstate> findByAddress_RoadAddress(String roadAddress);
}
