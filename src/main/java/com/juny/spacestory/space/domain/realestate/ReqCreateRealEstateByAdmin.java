package com.juny.spacestory.space.domain.realestate;

public record ReqCreateRealEstateByAdmin(Address address, Integer floor, Boolean hasParking, Boolean hasElevator, String hostId) {

}
