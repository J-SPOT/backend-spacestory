package com.juny.spacestory.space.domain.category;

import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapstruct {

  ResMainCategory toResMainCategory(MainCategory mainCategory);

  ResSubCategory toResSubCategory(SubCategory subCategory);

  List<ResOnlySubCategory> toResOnlySubCategories(List<SubCategory> subCategories);

  ResCategory toResCategory(MainCategory mainCategory);

  List<ResCategory> toResCategories(List<MainCategory> mainCategories);
}
