package com.juny.spacestory.mapper;

import com.juny.spacestory.domain.Space;
import com.juny.spacestory.dto.ResponseSpace;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SpaceMapper {
    @Mapping(source = "realEstate.address.sido", target = "sido")
    @Mapping(source = "realEstate.address.sigungu", target = "sigungu")
    @Mapping(source = "realEstate.address.dong", target = "dong")
    @Mapping(source = "realEstate.floor", target = "floor")
    @Mapping(source = "realEstate.hasParking", target = "hasParking")
    @Mapping(source = "realEstate.hasElevator", target = "hasElevator")
    ResponseSpace SpaceToResponseSpace(Space space);

    List<ResponseSpace> SpacesToResponseSpaces(List<Space> spaces);
}
