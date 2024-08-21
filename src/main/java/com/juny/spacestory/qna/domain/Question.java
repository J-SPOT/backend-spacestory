package com.juny.spacestory.qna.domain;

import com.juny.spacestory.qna.dto.ReqQuestion;
import com.juny.spacestory.space.domain.Space;
import com.juny.spacestory.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@ToString
@Table(name = "questions")
public class Question {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private String content;

  @Column
  private LocalDateTime createdAt;

  @ManyToOne
  @JoinColumn(name = "space_id")
  private Space space;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @OneToMany(mappedBy = "question")
  private List<Answer> answers = new ArrayList<>();

  public Question(String content) {
    this.content = content;
    createdAt = LocalDateTime.now();
  }

  // ManyToOne 연관관계 편의 메서드, 질문 - 유저 [양방향]
  public void setUser(User user) {
    if (this.user != null) {
      this.user.getQuestions().remove(this);
    }
    this.user = user;
    if (user != null && !user.getQuestions().contains(this)) {
      user.getQuestions().add(this);
    }
  }

  // ManyToOne 연관관계 편의 메서드, 질문 - 공간 [양방향]
  public void setSpace(Space space) {
    if (this.space != null) {
      this.space.getQuestions().remove(this);
    }
    this.space = space;
    if (space != null && !space.getQuestions().contains(this)) {
      space.getQuestions().add(this);
    }
  }

  // OneToMany 연관관계 편의 메서드, 질문 - 답변 [양방향]
  public void addAnswer(Answer answer) {
    this.answers.add(answer);
    if (answer.getQuestion() != this) {
      answer.setQuestion(this);
    }
  }

  // OneToMany 연관관계 편의 메서드, 질문 - 답변 [양방향]
  public void removeAnswer(Answer answer) {
    this.answers.remove(answer);
    if (answer.getQuestion() == this) {
      answer.setQuestion(null);
    }
  }

  public void updateContent(ReqQuestion req) {
    this.content = req.content();
  }
}
