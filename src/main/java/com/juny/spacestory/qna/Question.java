package com.juny.spacestory.qna;

import com.juny.spacestory.space.domain.Space;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class Question {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String content;

  private QuestionStatus questionStatus;

  private LocalDateTime createdAt;

  @ManyToOne
  @JoinColumn(name = "space_id")
  private Space space;

  @OneToMany(mappedBy = "question")
  private List<Answer> answers = new ArrayList<>();
}
