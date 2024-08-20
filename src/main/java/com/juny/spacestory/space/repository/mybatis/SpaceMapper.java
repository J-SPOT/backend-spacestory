package com.juny.spacestory.space.repository.mybatis;

import com.juny.spacestory.space.domain.Space;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SpaceMapper {

  List<Long> findSpaceIds(@Param("size") int offset, @Param("offset") int limit);

  List<Space> selectSpacesWithOptions(@Param("spaceIds") List<Long> spaceIds);

  int countAll();
}
