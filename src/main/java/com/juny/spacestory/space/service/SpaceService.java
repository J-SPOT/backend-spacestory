package com.juny.spacestory.space.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.juny.spacestory.global.exception.ErrorCode;
import com.juny.spacestory.global.exception.common.BadRequestException;
import com.juny.spacestory.space.domain.Space;
import com.juny.spacestory.space.domain.category.CategoryService;
import com.juny.spacestory.space.domain.category.MainCategory;
import com.juny.spacestory.space.domain.category.MainCategoryRepository;
import com.juny.spacestory.space.domain.category.ResOnlySubCategory;
import com.juny.spacestory.space.domain.category.SubCategory;
import com.juny.spacestory.space.domain.category.SubCategoryRepository;
import com.juny.spacestory.space.domain.hashtag.Hashtag;
import com.juny.spacestory.space.domain.hashtag.HashtagRepository;
import com.juny.spacestory.space.domain.option.Option;
import com.juny.spacestory.space.domain.option.OptionRepository;
import com.juny.spacestory.space.domain.realestate.RealEstate;
import com.juny.spacestory.space.domain.realestate.RealEstateRepository;
import com.juny.spacestory.space.domain.space_option.SpaceOption;
import com.juny.spacestory.space.dto.ReqSpace;
import com.juny.spacestory.space.dto.ResSpace;
import com.juny.spacestory.space.dto.ResSummarySpace;
import com.juny.spacestory.space.mapper.SpaceMapstruct;
import com.juny.spacestory.space.repository.SpaceRepository;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class SpaceService {

  private final SpaceRepository spaceRepository;
  private final RealEstateRepository realEstateRepository;
  private final SubCategoryRepository subCategoryRepository;
  private final MainCategoryRepository mainCategoryRepository;
  private final OptionRepository optionRepository;
  private final HashtagRepository hashtagRepository;
  private final SpaceMapstruct mapstruct;
  private final CategoryService categoryService;
  private final AmazonS3 amazonS3;

  private final String INVALID_SPACE_ID_MSG = "Invalid space id";
  private final String INVALID_REAL_ESTATE_ID_MSG = "Invalid real estate id";
  private final String INVALID_MAIN_CATEGORY_NAME_MSG = "Invalid main category name";
  private final String INVALID_SUB_CATEGORY_NAME_MSG = "Invalid sub category name";
  private final String INVALID_OPTION_NAME_MSG = "Invalid option name";
  private final String SUBCATEGORY_DOES_NOT_FIT_MSG = "It doesn't fit into the hierarchy. Subcategory: %s MainCategory: %s";
  private final String INVALID_USER_ID = "Invalid user id";
  private final String EXCEED_TEN_SPACE_IMAGES = "Exceed 10 space images";
  private final String INVALID_IMAGE_PATH = "Invalid image path";
  private final String INVALID_REAL_ESTATE_STATUS_MSG = "Invalid real estate status";

  @Value("${cloud.aws.s3.bucket}")
  private String bucketName;

  @Transactional
  public Page<ResSpace> findAllSpaces(int page, int size) {

    Page<Space> spaces = spaceRepository.findAll(PageRequest.of(page, size));

    return mapstruct.toResSpace(spaces);
  }

  @Transactional
  public ResSpace findSpaceById(Long spaceId) {

    Space space = spaceRepository.findById(spaceId).orElseThrow(
      () -> new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_SPACE_ID_MSG));

    space.increaseViewCount();

    return mapstruct.toResSpace(space);
  }

  @Transactional
  public Page<ResSpace> findSpacesByRealEstateId(Long realEstateId, int page, int size) {

    Page<Space> spaces = spaceRepository.findByRealEstateId(realEstateId,
      PageRequest.of(page, size));

    return mapstruct.toResSpace(spaces);
  }

  @Transactional
  public ResSpace createSpace(Long realEstateId, ReqSpace req) {

    Space space = new Space(req.name(), req.description(), req.reservationNotes(), req.openingTime(), req.closingTime(), req.hourlyRate(),
      req.spaceSize(),
      req.maxCapacity());

    setRealEstate(realEstateId, space);

    setCategory(req, space);

    setHashtag(req, space);

    Space savedSpace = spaceRepository.save(space);

    setOption(req, savedSpace);

    return mapstruct.toResSpace(savedSpace);
  }

  private void updateOptions(List<String> optionNames, Space space) {
    List<Option> newOptions = optionNames.stream()
      .map(optionName -> optionRepository.findByName(optionName)
        .orElseThrow(() -> new BadRequestException(ErrorCode.BAD_REQUEST, "Invalid option name")))
      .toList();

    List<Option> currentOptions = space.getSpaceOptions().stream()
      .map(SpaceOption::getOption)
      .toList();

    for (Option option : newOptions) {
      if (!currentOptions.contains(option)) {
        space.addOption(option);
      }
    }

    for (Option option : currentOptions) {
      if (!newOptions.contains(option)) {
        space.removeOption(option);
      }
    }
  }

  private void setHashtag(ReqSpace req, Space space) {
    for (var e : req.hashtags()) {
      Hashtag hashtag = hashtagRepository.findByName(e).orElseGet(
        () -> {
          Hashtag ht = new Hashtag(e);
          return hashtagRepository.save(ht);
        }
      );
      space.addHashtag(hashtag);
    }
  }

  private void setOption(ReqSpace req, Space space) {

    for (var e : req.options()) {
      Option option = optionRepository.findByName(e)
        .orElseThrow(() -> new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_OPTION_NAME_MSG));
      space.addOption(option);
    }
  }

  private void setCategory(ReqSpace req, Space space) {

    MainCategory mainCategory = mainCategoryRepository.findByName(req.mainCategory()).orElseThrow(
      () -> new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_MAIN_CATEGORY_NAME_MSG));

    List<String> subCategories = categoryService.findSubCategoriesByMainCategoryId(mainCategory.getId())
      .stream()
      .map(ResOnlySubCategory::name)
      .toList();

    for (var e : req.subCategories()) {
      if (!subCategories.contains(e)) {
        throw new BadRequestException(ErrorCode.BAD_REQUEST,
          String.format(SUBCATEGORY_DOES_NOT_FIT_MSG, e, mainCategory.getName()));
      }
      SubCategory subCategory = subCategoryRepository.findByName(e).orElseThrow(
        () -> new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_SUB_CATEGORY_NAME_MSG));
      subCategory.setMainCategory(mainCategory);
      space.addSubCategory(subCategory);
    }
  }

  private void setRealEstate(Long realEstateId, Space space) {
    RealEstate realEstate = realEstateRepository.findById(realEstateId).orElseThrow(
      () -> new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_REAL_ESTATE_ID_MSG));

    space.setRealEstate(realEstate);
  }

  @Transactional
  public ResSpace updateSpace(Long spaceId, ReqSpace req) {

    Space space = spaceRepository.findById(spaceId).orElseThrow(
      () -> new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_SPACE_ID_MSG));

    space.updateSpace(req);

    space.getSubCategories().clear();
    setCategory(req, space);

    space.getHashtags().clear();
    setHashtag(req, space);

    updateOptions(req.options(), space);

    return mapstruct.toResSpace(space);
  }

  public void deleteSpace(Long id) {

    Space space = spaceRepository.findById(id).orElseThrow(
      () -> new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_SPACE_ID_MSG));

    spaceRepository.delete(space);
  }

  @Transactional
  public Page<ResSpace> findAllSpacesByMostViews(int page, int size) {

    Page<Space> spaces = spaceRepository.findAllByOrderByViewCountDesc(
      PageRequest.of(page, size));

    return mapstruct.toResSpace(spaces);
  }

  @Transactional
  public Page<ResSpace> findAllSpacesByMostLikes(int page, int size) {

    Page<Space> spaces = spaceRepository.findAllByOrderByLikeCountDesc(
      PageRequest.of(page, size));

    return mapstruct.toResSpace(spaces);
  }

  @Transactional
  public Page<ResSpace> findAllRecentlyCreatedSpaces(int page, int size) {

    Page<Space> spaces = spaceRepository.findAllByOrderByCreatedAtDesc(
      PageRequest.of(page, size));

    return mapstruct.toResSpace(spaces);
  }

  public Page<ResSummarySpace> searchSpaces(String query, List<String> sigungu, Integer minCapacity,
    Integer minPrice, Integer maxPrice, List<String> options, String sort, int page, int size) {

    return spaceRepository.searchSpacesByFilter(query, sigungu, minCapacity, minPrice,
      maxPrice, options, sort, page, size);
  }

  @Transactional
  public ResSpace uploadImages(Long spaceId, UUID userId, List<MultipartFile> files)
    throws IOException {

    Space space = spaceRepository.findById(spaceId).orElseThrow(
      () -> new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_SPACE_ID_MSG));

    if (!space.getRealEstate().getUser().getId().equals(userId)) {

      throw new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_USER_ID);
    }

    if (space.getImagePaths().size() + files.size() > 10) {
      throw new BadRequestException(ErrorCode.BAD_REQUEST, EXCEED_TEN_SPACE_IMAGES);
    }

    for (var file : files) {
      String original = file.getOriginalFilename();
      String unique = UUID.randomUUID().toString() + "-" + original;

      ObjectMetadata metadata = new ObjectMetadata();
      metadata.setContentLength(file.getSize());
      amazonS3.putObject(bucketName, unique, file.getInputStream(), metadata);

      space.getImagePaths().add(unique);
    }

    return mapstruct.toResSpace(space);
  }

  @Transactional
  public void deleteImage(Long spaceId, UUID userId, List<String> imagePath) {

    Space space = spaceRepository.findById(spaceId).orElseThrow(
      () -> new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_SPACE_ID_MSG));

    if (!space.getRealEstate().getUser().getId().equals(userId)) {
      throw new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_USER_ID);
    }

    for (var image : imagePath) {
      if (!space.getImagePaths().contains(imagePath)) {
        throw new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_IMAGE_PATH);
      }
      amazonS3.deleteObject(bucketName, image);
      space.getImagePaths().remove(image);
    }
  }

  @Transactional
  public ResSpace setRepresentImage(Long spaceId, UUID userId, String imagePath) {
    Space space = spaceRepository.findById(spaceId).orElseThrow(
      () -> new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_SPACE_ID_MSG));

    if (!space.getRealEstate().getUser().getId().equals(userId)) {
      throw new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_USER_ID);
    }

    if (!space.getImagePaths().contains(imagePath)) {
      throw new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_IMAGE_PATH);
    }

    space.setRepresentImage(imagePath);

    return mapstruct.toResSpace(space);
  }
}
