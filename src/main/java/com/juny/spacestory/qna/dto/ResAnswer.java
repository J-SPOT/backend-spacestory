package com.juny.spacestory.qna.dto;

import java.time.LocalDateTime;

public record ResAnswer(
  Long id,
  String content,
  LocalDateTime createdAt,
  String email) {

}
