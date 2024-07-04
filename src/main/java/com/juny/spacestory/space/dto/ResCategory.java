package com.juny.spacestory.space.dto;

import java.util.List;

public record ResCategory(Long id, String name, List<ResSubCategory> subCategories) {

}
