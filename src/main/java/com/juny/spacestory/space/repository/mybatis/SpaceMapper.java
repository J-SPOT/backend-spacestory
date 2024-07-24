package com.juny.spacestory.space.repository.mybatis;

import com.juny.spacestory.space.domain.Space;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SpaceMapper {

  List<Space> findAll(@Param("size") int size, @Param("offset") int offset);

  int countAll();
}
