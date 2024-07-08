package com.juny.spacestory.space.domain.category;

import java.util.List;

public record ResCategory(Long id, String name, List<ResSubCategory> subCategories) {

}
