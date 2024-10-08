package com.juny.spacestory.space.domain.option;

import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OptionMapstruct {

  ResOption toResOption(Option option);

  List<ResOption> toResOptions(List<Option> options);
}
