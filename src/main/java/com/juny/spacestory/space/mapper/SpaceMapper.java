package com.juny.spacestory.space.mapper;

import com.juny.spacestory.space.domain.Space;
import com.juny.spacestory.space.dto.ResSpace;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SpaceMapper {

    ResSpace toResSpaces(Space space);

    List<ResSpace> toResSpaces(List<Space> spaces);
//  @Mapping(source = "realEstate.address.sido", target = "sido")
//  @Mapping(source = "realEstate.address.sigungu", target = "sigungu")
//  @Mapping(source = "realEstate.address.dong", target = "dong")
//  @Mapping(source = "realEstate.floor", target = "floor")
//  @Mapping(source = "realEstate.hasParking", target = "hasParking")
//  @Mapping(source = "realEstate.hasElevator", target = "hasElevator")
//  ResSpaces toResSpaces(Space space);
//
//  default Page<ResSpaces> toResSpaces(Page<Space> spaces) {
//    return spaces.map(this::toResSpaces);
//  }
}
