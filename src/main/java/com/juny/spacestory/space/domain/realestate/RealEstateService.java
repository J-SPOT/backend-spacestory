package com.juny.spacestory.space.domain.realestate;

import com.juny.spacestory.global.exception.ErrorCode;
import com.juny.spacestory.global.exception.common.BadRequestException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RealEstateService {

  private final RealEstateRepository realEstateRepository;
  private final RealEstateMapper mapper;

  private final String InvalidRealEstateIdMsg = "RealEstate id is invalid";
  private final String InvalidRequestCreateRealEstateForm = "RealEstate create form is null or empty";

  public List<ResRealEstate> findRealEstates() {

    List<RealEstate> realEstates = realEstateRepository.findAll();

    return mapper.toResRealEstates(realEstates);
  }

  public ResRealEstate findRealEstateById(Long id) {

    RealEstate realEstate = realEstateRepository.findById(id).orElseThrow(
      () -> new BadRequestException(ErrorCode.BAD_REQUEST, InvalidRealEstateIdMsg));

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
      throw new BadRequestException(ErrorCode.BAD_REQUEST, InvalidRequestCreateRealEstateForm);
    }

    if (req.address().getJibunAddress() == null || req.address().getJibunAddress().trim().isEmpty()
      || req.address().getRoadAddress() == null || req.address().getRoadAddress().trim().isEmpty()
      || req.address().getSido() == null || req.address().getSido().trim().isEmpty()
      || req.address().getSigungu() == null || req.address().getSigungu().trim().isEmpty()
      || req.address().getDong() == null || req.address().getDong().trim().isEmpty()) {

      throw new BadRequestException(ErrorCode.BAD_REQUEST, InvalidRequestCreateRealEstateForm);
    }
  }

  public ResRealEstate updateRealEstate(Long id, ReqRealEstate req) {

    RealEstate realEstate = realEstateRepository.findById(id).orElseThrow(
      () -> new BadRequestException(ErrorCode.BAD_REQUEST, InvalidRealEstateIdMsg));

    realEstate.updateRealEstateForm(req);

    return mapper.toResRealEstate(realEstate);
  }

  public void deleteRealEstate(Long id) {

    RealEstate realEstate = realEstateRepository.findById(id).orElseThrow(
      () -> new BadRequestException(ErrorCode.BAD_REQUEST, InvalidRealEstateIdMsg));

    realEstateRepository.delete(realEstate);
  }
}
