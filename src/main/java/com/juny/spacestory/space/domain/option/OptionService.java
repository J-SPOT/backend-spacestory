package com.juny.spacestory.space.domain.option;

import com.juny.spacestory.global.exception.ErrorCode;
import com.juny.spacestory.global.exception.common.BadRequestException;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OptionService {

  private final OptionRepository optionRepository;
  private final OptionMapper mapper;

  private final String OPTION_NOT_FOUND_MSG = "Option not found";

  public List<ResOption> findOptions() {

    List<Option> options = optionRepository.findAll(
      Sort.by(Order.asc("id")));

    return mapper.toResOptions(options);
  }


  public ResOption createOption(String name) {

    Option savedOption = optionRepository.save(new Option(name));

    return mapper.toResOption(savedOption);
  }

  @Transactional
  public ResOption modifyOption(Long id, String name) {

    Option option = getOptionById(id);

    option.changeOptionName(name);

    return mapper.toResOption(option);
  }

  public void deleteOption(Long id) {

    getOptionById(id);

    optionRepository.deleteById(id);
  }

  private Option getOptionById(Long id) {

    return optionRepository.findById(id).orElseThrow(
      () -> new BadRequestException(ErrorCode.BAD_REQUEST, OPTION_NOT_FOUND_MSG));
  }
}
