package com.juny.spacestory.qna.mapper;

import com.juny.spacestory.qna.domain.Answer;
import com.juny.spacestory.qna.domain.Question;
import com.juny.spacestory.qna.dto.ResAnswer;
import com.juny.spacestory.qna.dto.ResQuestion;
import com.juny.spacestory.space.mapper.SpaceMapper;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = SpaceMapper.class)
public interface QnaMapper {

  @Mapping(source = "user.email", target = "email", qualifiedByName = "maskEmail")
  ResAnswer toResAnswer(Answer answer);

  @Mapping(source = "user.email", target = "email", qualifiedByName = "maskEmail")
  ResQuestion toResQuestion(Question question);

  @Named("maskEmail")
  default String maskEmail(String userEmail) {

    String[] split = userEmail.split("@");

    int maskLength = split[0].length() / 3;

    Set<Integer> maskPositions = new HashSet<>();
    Random random = new Random();
    while (maskPositions.size() < maskLength) {
      maskPositions.add(random.nextInt(split[0].length()));
    }

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < split[0].length(); i++) {
      if (maskPositions.contains(i)) {
        sb.append('*');
        continue;
      }
      sb.append(split[0].charAt(i));
    }
    sb.append(split[1]);

    return sb.toString();
  }
}
