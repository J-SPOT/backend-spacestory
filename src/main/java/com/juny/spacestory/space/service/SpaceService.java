package com.juny.spacestory.space.service;

import com.juny.spacestory.global.exception.ErrorCode;
import com.juny.spacestory.global.exception.hierarchy.host.HostInvalidIdBusinessException;
import com.juny.spacestory.global.exception.hierarchy.space.SpaceInvalidDetailedTypeBusinessException;
import com.juny.spacestory.global.exception.hierarchy.space.SpaceInvalidIdBusinessException;
import com.juny.spacestory.host.Host;
import com.juny.spacestory.host.HostRepository;
import com.juny.spacestory.realestate.Address;
import com.juny.spacestory.realestate.RealEstate;
import com.juny.spacestory.realestate.RealEstateRepository;
import com.juny.spacestory.space.domain.SpaceType;
import com.juny.spacestory.space.domain.DetailedType;
import com.juny.spacestory.space.domain.Space;
import com.juny.spacestory.space.dto.RequestCreateSpace;
import com.juny.spacestory.space.dto.RequestUpdateSpace;
import com.juny.spacestory.space.dto.ResponseSpace;
import com.juny.spacestory.space.mapper.SpaceMapper;
import com.juny.spacestory.space.repository.SpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SpaceService {

  private final SpaceRepository spaceRepository;

  private final HostRepository hostRepository;

  private final RealEstateRepository realEstateRepository;

  private final SpaceMapper mapper;

  private static final Map<SpaceType, Set<DetailedType>> validDetailedTypesMap = Map.of(
    SpaceType.FRIENDSHIP, EnumSet.of(DetailedType.PARTY_ROOM, DetailedType.RESIDENCE, DetailedType.CAFE),
    SpaceType.EVENT, EnumSet.of(DetailedType.PERFORMANCE_VENUE, DetailedType.CONFERENCE_HALL, DetailedType.EXHIBITION_HALL),
    SpaceType.EDUCATION, EnumSet.of(DetailedType.STUDY_ROOM, DetailedType.LECTURE_ROOM, DetailedType.SEMINAR_ROOM, DetailedType.MEETING_ROOM),
    SpaceType.ART, EnumSet.of(DetailedType.DANCE_ROOM, DetailedType.VOCAL_ROOM, DetailedType.INSTRUMENT_ROOM, DetailedType.DRAWING_ROOM, DetailedType.CRAFT_ROOM),
    SpaceType.SPORT, EnumSet.of(DetailedType.BADMINTON_COURT, DetailedType.FUTSAL_COURT, DetailedType.TENNIS_COURT),
    SpaceType.PHOTOGRAPHY, EnumSet.of(DetailedType.FILM_STUDIO, DetailedType.BROADCAST_ROOM)
  );

  public List<ResponseSpace> searchByTypeInSeoul(SpaceType spaceType, int page, int size) {
    PageRequest pageRequest = PageRequest.of(page, size);

    List<Space> spaces = spaceRepository.findBySpaceTypeInSeoulQuerydsl(spaceType, pageRequest).getContent();
    return mapper.SpacesToResponseSpaces(spaces);
  }

  public List<ResponseSpace> searchSpaces(SpaceType spaceType, String sido, String sigungu, int minCapacity, Set<DetailedType> detailedType, int page, int size) {
    PageRequest pageRequest = PageRequest.of(page, size);

    List<Space> spaces = spaceRepository.findByCriteriaQuerydsl(spaceType, sido, sigungu, minCapacity, detailedType, pageRequest).getContent();
    return mapper.SpacesToResponseSpaces(spaces);
  }

  public ResponseSpace create(RequestCreateSpace req) {
    if (isInValidDetailedTypes(req.spaceType(), req.detailedTypes())) {
      throw new SpaceInvalidDetailedTypeBusinessException(ErrorCode.SPACE_INVALID_DETAILED_TYPE);
    }

    RealEstate findRealEstate = realEstateRepository.findByAddress_RoadAddress(req.roadAddress()).orElse(null);
    if (findRealEstate == null) {
      Address address = new Address(req.roadAddress(), req.jibunAddress(), req.sido(), req.sigungu(), req.dong());
      RealEstate realEstate = new RealEstate(address, req.floor(), req.hasParking(), req.hasElevator(), false, findByHostId(req.hostId()));
      findRealEstate = realEstateRepository.save(realEstate);
    }

    Space space = new Space(req.spaceType(), req.spaceName(), req.openingTime(), req.closingTime(), req.hourlyRate(), req.spaceSize(), req.maxCapacity(), req.spaceDescription(), req.isDeleted(), req.detailedTypes(), findRealEstate);
    Space savedSpace = spaceRepository.save(space);
    return mapper.SpaceToResponseSpace(savedSpace);
  }

  private boolean isInValidDetailedTypes(SpaceType spaceType, Set<DetailedType> detailedTypes) {
    Set<DetailedType> validDetailedTypes = validDetailedTypesMap.getOrDefault(spaceType, EnumSet.noneOf(DetailedType.class));
    return !validDetailedTypes.containsAll(detailedTypes);
  }

  public ResponseSpace update(Long spaceId, RequestUpdateSpace req) {
    if (isInValidDetailedTypes(req.spaceType(), req.detailedTypes())) {
      throw new SpaceInvalidDetailedTypeBusinessException(ErrorCode.SPACE_INVALID_DETAILED_TYPE);
    }
    Space space = findBySpaceId(spaceId);
    space.updateSpace(req);
    Space updatedSpace = spaceRepository.save(space);

    return mapper.SpaceToResponseSpace(updatedSpace);
  }

  public void delete(Long spaceId) {
    Space space = findBySpaceId(spaceId);
    space.softDelete(space);
    spaceRepository.save(space);
  }

  private Space findBySpaceId(Long spaceId) {
    return spaceRepository
        .findByIdAndIsDeletedFalse(spaceId)
        .orElseThrow(() -> new SpaceInvalidIdBusinessException(ErrorCode.SPACE_INVALID_ID));
  }

  private Host findByHostId(Long hostId) {
    return hostRepository
        .findById(hostId)
        .orElseThrow(() -> new HostInvalidIdBusinessException(ErrorCode.HOST_INVALID_ID));
  }
}