package com.juny.spacestory.space.domain.category;

import com.juny.spacestory.global.exception.ErrorCode;
import com.juny.spacestory.global.exception.common.BadRequestException;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

  private final MainCategoryRepository mainCategoryRepository;
  private final SubCategoryRepository subCategoryRepository;
  private final CategoryMapstruct mapstruct;

  private final String MAIN_CATEGORY_NOT_FOUND_MSG = "Main category not found";
  private final String SUB_CATEGORY_NOT_FOUND_MSG = "Sub category not found";
  private final String CATEGORY_HIERARCHY_INVALID_MSG = "Sub category does not belong to the main category";

  @Transactional
  public List<ResCategory> findCategories() {

    List<MainCategory> allCategories = mainCategoryRepository.findAllCategories();

    return mapstruct.toResCategories(allCategories);
  }

  public ResMainCategory createMainCategory(String name) {

    MainCategory savedCategory = mainCategoryRepository.save(new MainCategory(name));

    return mapstruct.toResMainCategory(savedCategory);
  }

  @Transactional
  public ResMainCategory modifyMainCategoryById(Long id, String newName) {

    MainCategory mainCategory = getMainCategory(id);

    mainCategory.changeCategoryName(newName);

    return mapstruct.toResMainCategory(mainCategory);
  }

  public void deleteById(Long id) {

    getMainCategory(id);

    mainCategoryRepository.deleteById(id);
  }

  public ResSubCategory createSubCategory(Long mainId, String name) {

    MainCategory mainCategory = getMainCategory(mainId);
    SubCategory subCategory = new SubCategory(name);

    subCategory.setMainCategory(mainCategory);

    SubCategory savedSubCategory = subCategoryRepository.save(subCategory);

    return mapstruct.toResSubCategory(savedSubCategory);
  }

  public List<ResOnlySubCategory> findSubCategoriesByMainCategoryId(Long mainId) {

    getMainCategory(mainId);

    List<SubCategory> subCategories = subCategoryRepository.findByMainCategory_Id(mainId);
    
    return mapstruct.toResOnlySubCategories(subCategories);
  }

  @Transactional
  public ResSubCategory modifySubCategory(Long id, Long subId, String newName) {

    SubCategory subCategory = getSubCategory(subId);

    checkCategoryHierarchy(id, subCategory);

    subCategory.changeCategoryName(newName);

    return mapstruct.toResSubCategory(subCategory);
  }

  public void deleteSubCategory(Long id, Long subId) {

    SubCategory subCategory = getSubCategory(subId);

    checkCategoryHierarchy(id, subCategory);

    subCategoryRepository.delete(subCategory);
  }

  private MainCategory getMainCategory(Long id) {

    MainCategory mainCategory = mainCategoryRepository.findById(id).orElseThrow(
      () -> new BadRequestException(ErrorCode.BAD_REQUEST, MAIN_CATEGORY_NOT_FOUND_MSG));

    return mainCategory;
  }

  private SubCategory getSubCategory(Long subId) {

    SubCategory subCategory = subCategoryRepository.findById(subId).orElseThrow(
      () -> new BadRequestException(ErrorCode.BAD_REQUEST, SUB_CATEGORY_NOT_FOUND_MSG));

    return subCategory;
  }

  private void checkCategoryHierarchy(Long id, SubCategory subCategory) {

    if (!subCategory.getMainCategory().getId().equals(id)) {
      throw new BadRequestException(ErrorCode.BAD_REQUEST, CATEGORY_HIERARCHY_INVALID_MSG);
    }
  }
}
