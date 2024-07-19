package com.juny.spacestory.qna.mapper;

import com.juny.spacestory.qna.domain.Answer;
import com.juny.spacestory.qna.domain.Question;
import com.juny.spacestory.qna.dto.ResAnswer;
import com.juny.spacestory.qna.dto.ResQuestion;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomMapper {

  public Page<ResQuestion> toResQuestions(Page<Question> questions) {

    return questions.map(this::toResQuestionNoRecursive);
  }

  private ResQuestion toResQuestionNoRecursive(Question question) {

    List<ResAnswer> answers = question.getAnswers().stream().map(this::toResAnswer)
      .collect(Collectors.toList());

    return new ResQuestion(
      question.getId(),
      question.getContent(),
      question.getCreatedAt(),
      question.getUser().getEmail(),
      answers
    );
  }

  private ResAnswer toResAnswer(Answer answer) {

    return new ResAnswer(
      answer.getId(),
      answer.getContent(),
      answer.getCreatedAt(),
      answer.getUser().getEmail()
    );
  }
}
