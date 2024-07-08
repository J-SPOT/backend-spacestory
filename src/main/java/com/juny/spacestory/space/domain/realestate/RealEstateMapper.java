package com.juny.spacestory.space.domain.realestate;

import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RealEstateMapper {

  ResRealEstate toResRealEstate(RealEstate realEstate);

  List<ResRealEstate> toResRealEstates(List<RealEstate> realEstates);
}
