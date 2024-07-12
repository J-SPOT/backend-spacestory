package com.juny.spacestory.space.service;

import com.juny.spacestory.global.exception.ErrorCode;
import com.juny.spacestory.global.exception.common.BadRequestException;
import com.juny.spacestory.space.domain.Space;
import com.juny.spacestory.space.domain.category.CategoryService;
import com.juny.spacestory.space.domain.category.MainCategory;
import com.juny.spacestory.space.domain.category.MainCategoryRepository;
import com.juny.spacestory.space.domain.category.ResSubCategory;
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
import com.juny.spacestory.space.mapper.SpaceMapper;
import com.juny.spacestory.space.repository.SpaceRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SpaceService {

  private final SpaceRepository spaceRepository;
  private final RealEstateRepository realEstateRepository;
  private final SubCategoryRepository subCategoryRepository;
  private final MainCategoryRepository mainCategoryRepository;
  private final OptionRepository optionRepository;
  private final HashtagRepository hashtagRepository;
  private final SpaceMapper mapper;
  private final CategoryService categoryService;

  private final String INVALID_SPACE_ID_MSG = "Invalid space id";
  private final String INVALID_REAL_ESTATE_ID_MSG = "Invalid real estate id";
  private final String INVALID_MAIN_CATEGORY_NAME_MSG = "Invalid main category name";
  private final String INVALID_SUB_CATEGORY_NAME_MSG = "Invalid sub category name";
  private final String INVALID_OPTION_NAME_MSG = "Invalid option name";
  private final String SUBCATEGORY_DOES_NOT_FIT_MSG = "It doesn't fit into the hierarchy. Subcategory: %s MainCategory: %s";
  private final String EXCEEDED_MAXIMUM_THREE_SELECTION_SIGUNGU_MSG = "Exceeded the maximum of 3 selections SIGUNGU";


  public Page<ResSpace> findAllSpaces(int page, int size) {

    Page<Space> spaces = spaceRepository.findAll(PageRequest.of(page, size));

    return mapper.toResSpace(spaces);
  }

  public ResSpace findSpaceById(Long spaceId) {

    Space space = spaceRepository.findById(spaceId).orElseThrow(
      () -> new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_SPACE_ID_MSG));

    space.increaseViewCount();

    return mapper.toResSpace(space);
  }

  public Page<ResSpace> findSpacesByRealEstateId(Long realEstateId, int page, int size) {

    Page<Space> spaces = spaceRepository.findByRealEstateId(realEstateId,
      PageRequest.of(page, size));

    return mapper.toResSpace(spaces);
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

    return mapper.toResSpace(savedSpace);
  }

  private void updateOptions(List<String> optionNames, Space space) {
    List<Option> newOptions = optionNames.stream()
      .map(optionName -> optionRepository.findByName(optionName)
        .orElseThrow(() -> new BadRequestException(ErrorCode.BAD_REQUEST, "Invalid option name")))
      .collect(Collectors.toList());

    List<Option> currentOptions = space.getSpaceOptions().stream()
      .map(SpaceOption::getOption)
      .collect(Collectors.toList());

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
      .map(ResSubCategory::name)
      .collect(Collectors.toList());

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

    return mapper.toResSpace(space);
  }

  public void deleteSpace(Long id) {

    Space space = spaceRepository.findById(id).orElseThrow(
      () -> new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_SPACE_ID_MSG));

    spaceRepository.delete(space);
  }

  public Page<ResSpace> findAllSpacesByMostViews(int page, int size) {

    Page<Space> spaces = spaceRepository.findAllByOrderByViewCountDesc(
      PageRequest.of(page, size));

    return mapper.toResSpace(spaces);
  }

  public Page<ResSpace> findAllSpacesByMostLikes(int page, int size) {

    Page<Space> spaces = spaceRepository.findAllByOrderByLikeCountDesc(
      PageRequest.of(page, size));

    return mapper.toResSpace(spaces);
  }

  public Page<ResSpace> findAllRecentlyCreatedSpaces(int page, int size) {

    Page<Space> spaces = spaceRepository.findAllByOrderByCreatedAtDesc(
      PageRequest.of(page, size));

    return mapper.toResSpace(spaces);
  }

  public Page<ResSpace> searchSpaces(String query, List<String> sigungu, Integer minCapacity,
    Integer minPrice, Integer maxPrice, List<String> options, String sort, Pageable pageable) {

    if (sigungu.size() > 3) {
      throw new BadRequestException(ErrorCode.BAD_REQUEST, EXCEEDED_MAXIMUM_THREE_SELECTION_SIGUNGU_MSG);
    }

    Page<Space> spaces = spaceRepository.searchSpacesByFilter(query, sigungu, minCapacity, minPrice,
      maxPrice, options, sort, pageable);

    return mapper.toResSpace(spaces);
  }
}
