package com.juny.spacestory.qna.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ResQuestion(
  Long id,
  String content,
  LocalDateTime createdAt,
  String email,
  List<ResAnswer> answers
) {

}
