package com.juny.spacestory.space.mapper;

import com.juny.spacestory.space.domain.Space;
import com.juny.spacestory.space.domain.category.ResSubCategory;
import com.juny.spacestory.space.domain.category.SubCategory;
import com.juny.spacestory.space.domain.option.Option;
import com.juny.spacestory.space.domain.option.ResOption;
import com.juny.spacestory.space.domain.space_option.SpaceOption;
import com.juny.spacestory.space.dto.ResSpace;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface SpaceMapstruct {

  @Mapping(source = "spaceOptions", target = "spaceOptions", qualifiedByName = "spaceOptionToResOptions")
  @Mapping(source = "subCategories", target = "subCategories", qualifiedByName = "subCategoriesToResSubCategories")
  ResSpace toResSpace(Space space);

  default Page<ResSpace> toResSpace(Page<Space> spaces) {
    return spaces.map(this::toResSpace);
  }

  @Named("spaceOptionToResOptions")
  default List<ResOption> spaceOptionsToResOptions(List<SpaceOption> spaceOptions) {

    return spaceOptions.stream()
      .map(spaceOption -> {
        Option option = spaceOption.getOption();
        ResOption resOption = new ResOption(option.getId(), option.getName());
        return resOption;
      })
      .collect(Collectors.toList());
  }

  @Named("subCategoriesToResSubCategories")
  default List<ResSubCategory> subCategoriesToResSubCategories(List<SubCategory> subCategories) {
    return subCategories.stream()
      .map(subCategory -> new ResSubCategory(
        subCategory.getId(),
        subCategory.getName(),
        subCategory.getMainCategory().getId(),
        subCategory.getMainCategory().getName()
      ))
      .collect(Collectors.toList());
  }
}

