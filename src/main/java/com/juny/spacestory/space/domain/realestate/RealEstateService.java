package com.juny.spacestory.space.domain.realestate;

import com.juny.spacestory.global.exception.ErrorCode;
import com.juny.spacestory.global.exception.common.BadRequestException;
import com.juny.spacestory.user.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RealEstateService {

  private final RealEstateRepository realEstateRepository;
  private final RealEstateMapper mapper;
  private final UserRepository userRepository;

  private final String INVALID_REAL_ESTATE_ID = "RealEstate id is invalid";
  private final String INVALID_REQ_CREATE_FORM = "RealEstate create form is null or empty";
  private final String NON_EXISTENT_USER = "User not found";

  public Page<ResRealEstate> findRealEstates(int page, int size) {

    Page<RealEstate> realEstates = realEstateRepository.findAll(PageRequest.of(page, size));

    return mapper.toResRealEstates(realEstates);
  }

  public Page<ResRealEstate> findRealEstatesByUserId(UUID uuid, int page, int size) {

    userRepository.findById(uuid).orElseThrow(
      () -> new BadRequestException(ErrorCode.BAD_REQUEST, NON_EXISTENT_USER));

    Page<RealEstate> realEstates =
      realEstateRepository.findByUserId(uuid, PageRequest.of(page, size));

    return mapper.toResRealEstates(realEstates);
  }

  public ResRealEstate findRealEstateById(Long id) {

    RealEstate realEstate = realEstateRepository.findById(id).orElseThrow(
      () -> new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_REAL_ESTATE_ID));

    return mapper.toResRealEstate(realEstate);
  }


  public ResRealEstate createRealEstate(ReqRealEstate req) {

    validateReqCreateForm(req);

    RealEstate savedRealEstate = realEstateRepository.save(
      new RealEstate(req.address(), req.floor(), req.hasParking(), req.hasElevator()));

    return mapper.toResRealEstate(savedRealEstate);
  }

  private void validateReqCreateForm(ReqRealEstate req) {

    if (req == null || req.address() == null || req.floor() == null || req.hasParking() == null
      || req.hasElevator() == null) {
      throw new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_REQ_CREATE_FORM);
    }

    if (req.address().getJibunAddress() == null || req.address().getJibunAddress().trim().isEmpty()
      || req.address().getRoadAddress() == null || req.address().getRoadAddress().trim().isEmpty()
      || req.address().getSido() == null || req.address().getSido().trim().isEmpty()
      || req.address().getSigungu() == null || req.address().getSigungu().trim().isEmpty()
      || req.address().getDong() == null || req.address().getDong().trim().isEmpty()) {

      throw new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_REQ_CREATE_FORM);
    }
  }

  public ResRealEstate updateRealEstate(Long id, ReqRealEstate req) {

    RealEstate realEstate = realEstateRepository.findById(id).orElseThrow(
      () -> new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_REAL_ESTATE_ID));

    realEstate.updateRealEstateForm(req);

    return mapper.toResRealEstate(realEstate);
  }

  public void deleteRealEstate(Long id) {

    RealEstate realEstate = realEstateRepository.findById(id).orElseThrow(
      () -> new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_REAL_ESTATE_ID));

    realEstateRepository.delete(realEstate);
  }
}
