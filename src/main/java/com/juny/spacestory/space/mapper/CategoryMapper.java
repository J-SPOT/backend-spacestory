package com.juny.spacestory.space.mapper;

import com.juny.spacestory.space.domain.MainCategory;
import com.juny.spacestory.space.domain.SubCategory;
import com.juny.spacestory.space.dto.ResCategory;
import com.juny.spacestory.space.dto.ResMainCategory;
import com.juny.spacestory.space.dto.ResSubCategory;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

  ResMainCategory toResMainCategory(MainCategory mainCategory);

  ResSubCategory toResSubCategory(SubCategory subCategory);

  List<ResMainCategory> toResMainCategories(List<MainCategory> mainCategories);
  List<ResSubCategory> toResSubCategories(List<SubCategory> subCategories);

  ResCategory toResCategory(MainCategory mainCategory);
  List<ResCategory> toResCategories(List<MainCategory> mainCategories);
}
