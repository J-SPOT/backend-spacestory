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

  @OneToMany(mappedBy = "question")
  private List<Answer> answers = new ArrayList<>();

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  public Question(String content) {
    this.content = content;
    createdAt = LocalDateTime.now();
  }

  // 연관관계 편의 메서드
  public void setUser(User user) {
    if (this.user != null) {
      this.user.getQuestions().remove(this);
    }
    this.user = user;
    if (user != null && !user.getQuestions().contains(this)) {
      user.getQuestions().add(this);
    }
  }

  // 연관관계 편의 메서드
  public void setSpace(Space space) {
    if (this.space != null) {
      this.space.getQuestions().remove(this);
    }
    this.space = space;
    if (space != null && !space.getQuestions().contains(this)) {
      space.getQuestions().add(this);
    }
  }

  // 연관관계 편의 메서드
  public void addAnswer(Answer answer) {
    this.answers.add(answer);
    if (answer.getQuestion() != this) {
      answer.setQuestion(this);
    }
  }

  // 연관관계 편의 메서드
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
