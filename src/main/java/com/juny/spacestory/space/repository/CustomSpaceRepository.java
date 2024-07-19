package com.juny.spacestory.space.repository;

import com.juny.spacestory.space.domain.Space;
import java.util.List;
import org.springframework.data.domain.Page;

public interface CustomSpaceRepository {

  Page<Space> searchSpacesByFilter(
    String query,
    List<String> sigungu,
    Integer minCapacity,
    Integer minPrice,
    Integer maxPrice,
    List<String> options,
    String sort,
    int page,
    int size
  );
}
