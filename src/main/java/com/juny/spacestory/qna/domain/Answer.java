package com.juny.spacestory.qna.domain;

import com.juny.spacestory.qna.dto.ReqAnswer;
import com.juny.spacestory.user.domain.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "answers")
public class Answer {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private String content;

  @Column
  private LocalDateTime createdAt;

  @OneToOne
  @JoinColumn(name = "parent_id")
  private Answer parent;

  @OneToOne(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
  private Answer child;

  @ManyToOne
  @JoinColumn(name = "question_id")
  private Question question;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  public Answer(String content) {
    this.content = content;
    this.createdAt = LocalDateTime.now();
  }

  // ManyToOne 연관관계 편의 메서드, 답변 - 유저 [양방향]
  public void setUser(User user) {
    if (this.user != null) {
      this.user.getAnswers().remove(this);
    }
    this.user = user;
    if (user != null && !user.getAnswers().contains(this)) {
      user.getAnswers().add(this);
    }
  }

  // ManyToOne 연관관계 편의 메서드, 답변 - 질문 [양방향]
  public void setQuestion(Question question) {
    if (this.question != null) {
      this.question.getAnswers().remove(this);
    }
    this.question = question;
    if (question != null && !question.getAnswers().contains(this)) {
      question.getAnswers().add(this);
    }
  }

  // OneToOne 연관관계 편의 메서드, 답변 - 답변 [양방향]
  public void setParent(Answer parent) {
    if (this.parent != null) {
      this.parent.child = null;
    }
    this.parent = parent;
    if (parent != null && parent.child != this) {
      parent.child = this;
    }
  }

  public void updateContent(ReqAnswer req) {
    this.content = req.content();
  }
}
