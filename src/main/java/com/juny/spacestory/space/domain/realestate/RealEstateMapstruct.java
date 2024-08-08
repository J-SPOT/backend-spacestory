package com.juny.spacestory.space.domain.realestate;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface RealEstateMapstruct {

  ResRealEstate toResRealEstate(RealEstate realEstate);

  default Page<ResRealEstate> toResRealEstates(Page<RealEstate> realEstates) {
    return realEstates.map(this::toResRealEstate);
  }
}
