//package com.juny.spacestory.space.domain.realestate;
//
//import com.juny.spacestory.global.exception.ErrorCode;
//import com.juny.spacestory.global.exception.common.BadRequestException;
//import com.juny.spacestory.user.domain.User;
//import com.juny.spacestory.user.repository.UserRepository;
//import jakarta.transaction.Transactional;
//import java.util.UUID;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class RealEstateService {
//
//  private final RealEstateRepository realEstateRepository;
//  private final RealEstateMapstruct mapstruct;
//  private final UserRepository userRepository;
//
//  private final String INVALID_REAL_ESTATE_ID = "RealEstate id is invalid";
//  private final String INVALID_REQ_CREATE_FORM = "RealEstate create form is null or empty";
//  private final String NON_EXISTENT_USER = "User not found";
//  private final String INVALID_REAL_ESTATE_STATUS = "Invalid real estate status";
//  private final String INVALID_HOST_ID = "Invalid host id";
//
//  public Page<ResRealEstate> findRealEstates(int page, int size) {
//
//    Page<RealEstate> realEstates = realEstateRepository.findAll(PageRequest.of(page, size));
//
//    return mapstruct.toResRealEstates(realEstates);
//  }
//
//  public Page<ResRealEstate> findRealEstatesByUserId(UUID uuid, int page, int size) {
//
//    userRepository.findById(uuid).orElseThrow(
//      () -> new BadRequestException(ErrorCode.BAD_REQUEST, NON_EXISTENT_USER));
//
//    Page<RealEstate> realEstates =
//      realEstateRepository.findByUserIdAndDeletedAtIsNull(uuid, PageRequest.of(page, size));
//
//    return mapstruct.toResRealEstates(realEstates);
//  }
//
//  public ResRealEstate findRealEstateById(Long id) {
//
//    RealEstate realEstate = realEstateRepository.findById(id).orElseThrow(
//      () -> new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_REAL_ESTATE_ID));
//
//    return mapstruct.toResRealEstate(realEstate);
//  }
//
//
//  @Transactional
//  public ResRealEstate createRealEstate(ReqCreateRealEstateByAdmin req, UUID uuid) {
//
//    validateReqCreateForm(req);
//
//    RealEstate savedRealEstate = realEstateRepository.save(
//      new RealEstate(req.address(), req.floor(), req.hasParking(), req.hasElevator(), RealEstateStatus.대기));
//
//    User user = userRepository.findById(uuid).orElseThrow(
//      () -> new BadRequestException(ErrorCode.BAD_REQUEST, NON_EXISTENT_USER));
//
//    savedRealEstate.setHost(user);
//
//    return mapstruct.toResRealEstate(savedRealEstate);
//  }
//
//  @Transactional
//  public ResRealEstate createRealEstateByAdmin(ReqCreateRealEstateByAdmin req) {
//
//    validateReqCreateForm(req);
//
//    RealEstate savedRealEstate = realEstateRepository.save(
//      new RealEstate(req.address(), req.floor(), req.hasParking(), req.hasElevator(), RealEstateStatus.승인));
//
//    User user = userRepository.findById(UUID.fromString(req.hostId())).orElseThrow(
//      () -> new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_HOST_ID));
//
//    savedRealEstate.setHost(user);
//
//    return mapstruct.toResRealEstate(savedRealEstate);
//  }
//
//  private void validateReqCreateForm(ReqCreateRealEstateByAdmin req) {
//
//    if (req == null || req.address() == null || req.floor() == null || req.hasParking() == null
//      || req.hasElevator() == null) {
//      throw new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_REQ_CREATE_FORM);
//    }
//
//    if (req.address().getJibunAddress() == null || req.address().getJibunAddress().trim().isEmpty()
//      || req.address().getRoadAddress() == null || req.address().getRoadAddress().trim().isEmpty()
//      || req.address().getSido() == null || req.address().getSido().trim().isEmpty()
//      || req.address().getSigungu() == null || req.address().getSigungu().trim().isEmpty()
//      || req.address().getDong() == null || req.address().getDong().trim().isEmpty()) {
//
//      throw new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_REQ_CREATE_FORM);
//    }
//  }
//
//  @Transactional
//  public ResRealEstate approveRealEstate(Long realEstateId) {
//
//    RealEstate realEstate = realEstateRepository.findById(realEstateId).orElseThrow(
//      () -> new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_REAL_ESTATE_ID));
//
//    if (realEstate.getStatus() != RealEstateStatus.승인) {
//      throw new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_REAL_ESTATE_STATUS);
//    }
//
//    realEstate.approveRealEstate();
//
//    return mapstruct.toResRealEstate(realEstate);
//  }
//
//  @Transactional
//  public ResRealEstate cancelRealEstate(Long realEstateId) {
//    RealEstate realEstate = realEstateRepository.findById(realEstateId).orElseThrow(
//      () -> new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_REAL_ESTATE_ID));
//
//    if (realEstate.getStatus() != RealEstateStatus.취소) {
//      throw new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_REAL_ESTATE_STATUS);
//    }
//
//    realEstate.cancelRealEstate();
//
//    return mapstruct.toResRealEstate(realEstate);
//  }
//
//  @Transactional
//  public ResRealEstate updateRealEstate(Long realEstateId, ReqCreateRealEstateByAdmin req, UUID hostId) {
//
//    RealEstate realEstate = realEstateRepository.findById(realEstateId).orElseThrow(
//      () -> new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_REAL_ESTATE_ID));
//
//    if (realEstate.getStatus() != RealEstateStatus.승인) {
//      throw new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_REAL_ESTATE_STATUS);
//    }
//
//    if (!realEstate.getUser().getId().equals(hostId)) {
//      throw new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_HOST_ID);
//    }
//
//    realEstate.updateRealEstateForm(req, RealEstateStatus.대기);
//
//    return mapstruct.toResRealEstate(realEstate);
//  }
//
//  @Transactional
//  public ResRealEstate updateRealEstateByAdmin(Long realEstateId, ReqCreateRealEstateByAdmin req) {
//
//    RealEstate realEstate = realEstateRepository.findById(realEstateId).orElseThrow(
//      () -> new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_REAL_ESTATE_ID));
//
//    realEstate.updateRealEstateForm(req, RealEstateStatus.승인);
//
//    return mapstruct.toResRealEstate(realEstate);
//  }
//
//  @Transactional
//  public void deleteRealEstate(Long realEstateId, UUID hostId) {
//
//    RealEstate realEstate = realEstateRepository.findById(realEstateId).orElseThrow(
//      () -> new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_REAL_ESTATE_ID));
//
//    if (!realEstate.getUser().getId().equals(hostId)) {
//      throw new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_HOST_ID);
//    }
//
//    realEstate.deleteSoft();
//  }
//
//  @Transactional
//  public void deleteRealEstateByAdmin(Long id) {
//
//    RealEstate realEstate = realEstateRepository.findById(id).orElseThrow(
//      () -> new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_REAL_ESTATE_ID));
//
//    realEstate.deleteSoft();
//  }
//}
