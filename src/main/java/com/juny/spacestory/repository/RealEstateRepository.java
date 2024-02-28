package com.juny.spacestory.repository;

import com.juny.spacestory.domain.RealEstate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RealEstateRepository extends JpaRepository<RealEstate, Long> {
    Optional<RealEstate> findByAddress_RoadAddress(String roadAddress);
}
