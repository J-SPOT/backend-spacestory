package com.juny.spacestory.qna.repository;

import com.juny.spacestory.qna.domain.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {

  Page<Question> findBySpaceId(Long spaceId, Pageable pageable);
}
