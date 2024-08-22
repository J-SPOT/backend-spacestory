//package com.juny.spacestory.space.domain.realestate;
//
//import java.util.UUID;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//public interface RealEstateRepository extends JpaRepository<RealEstate, Long> {
//
//  Page<RealEstate> findAll(Pageable pageable);
//
//  Page<RealEstate> findByUserIdAndDeletedAtIsNull(UUID userId, Pageable pageable);
//}
