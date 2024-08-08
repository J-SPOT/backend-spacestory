package com.juny.spacestory.space.domain.realestate;

public record ResRealEstate(Long id, Address address, Integer floor, Boolean hasParking, Boolean hasElevator, RealEstateStatus status) {

}
