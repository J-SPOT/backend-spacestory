package com.juny.spacestory.repository;

import com.juny.spacestory.domain.RealEstate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RealEstateRepository extends JpaRepository<RealEstate, Long> {
}
