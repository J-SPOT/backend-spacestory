package com.juny.spacestory.qna.service;

import com.juny.spacestory.global.exception.ErrorCode;
import com.juny.spacestory.global.exception.common.BadRequestException;
import com.juny.spacestory.qna.domain.Answer;
import com.juny.spacestory.qna.domain.Question;
import com.juny.spacestory.qna.dto.ReqAnswer;
import com.juny.spacestory.qna.dto.ReqQuestion;
import com.juny.spacestory.qna.dto.ResAnswer;
import com.juny.spacestory.qna.dto.ResQuestion;
import com.juny.spacestory.qna.mapper.CustomMapper;
import com.juny.spacestory.qna.mapper.QnaMapper;
import com.juny.spacestory.qna.repository.AnswerRepository;
import com.juny.spacestory.qna.repository.QuestionRepository;
import com.juny.spacestory.space.domain.Space;
import com.juny.spacestory.space.repository.SpaceRepository;
import com.juny.spacestory.user.domain.User;
import com.juny.spacestory.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QnaService {

  private final QuestionRepository questionRepository;

  private final AnswerRepository answerRepository;

  private final SpaceRepository spaceRepository;

  private final UserRepository userRepository;

  private final QnaMapper mapper;

  private final CustomMapper customMapper;

  private final String INVALID_USER_ID = "Invalid user id";
  private final String USER_ID_NOT_MATCH = "Invalid user id, expected: %s, actual: %s";
  private final String FIRST_ANSWER_IS_NOT_HOST = "Invalid host id, host: %s, user: %s";
  private final String INVALID_SPACE_ID = "Invalid space id";
  private final String INVALID_QUESTION_ID = "Invalid question id";
  private final String INVALID_PARENT_ANSWER_ID = "Invalid parent answer id";
  private final String ALREADY_CREATED_ANSWER = "Already created answer";
  private final String WAIT_FOR_REPLY = "Wait for a response";
  private final String ANSWERED_QUESTION_NOT_EDITED = "Answered question can't be edited";
  private final String ANSWERED_ANSWER_NOT_EDITED = "Answered answer can't be edited";

  public ResQuestion createQuestion(Long spaceId, ReqQuestion req, UUID userId) {

    User user = userRepository.findById(userId).orElseThrow(
      () -> new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_USER_ID));

    Space space = spaceRepository.findById(spaceId).orElseThrow(
      () -> new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_SPACE_ID));

    Question question = new Question(req.content());
    question.setUser(user);
    question.setSpace(space);

    questionRepository.save(question);

    return mapper.toResQuestion(question);
  }

  public ResAnswer createAnswer(Long questionId, ReqAnswer req, UUID userId) {

    Question question = questionRepository.findById(questionId).orElseThrow(
      () -> new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_QUESTION_ID));

    User user = userRepository.findById(userId).orElseThrow(
      () -> new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_USER_ID));

    Answer answer = new Answer(req.content());
    answer.setUser(user);
    answer.setQuestion(question);

    UUID hostId = question.getSpace().getRealEstate().getUser().getId();
    if (req.parentId() == null && !hostId.equals(userId)) {

      throw new BadRequestException(ErrorCode.BAD_REQUEST, String.format(FIRST_ANSWER_IS_NOT_HOST, hostId, userId));
    }

    if (req.parentId() != null) {
      Answer parent = answerRepository.findById(req.parentId()).orElseThrow(
        () -> new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_PARENT_ANSWER_ID));

      if (parent.getUser().getId().equals(userId)) {
        throw new BadRequestException(ErrorCode.BAD_REQUEST, WAIT_FOR_REPLY);
      }

      if (!Objects.isNull(parent.getChild())) {
        throw new BadRequestException(ErrorCode.BAD_REQUEST, ALREADY_CREATED_ANSWER);
      }
      answer.setParent(parent);
    }

    Answer savedAnswer = answerRepository.save(answer);

    return mapper.toResAnswer(savedAnswer);
  }

  public Page<ResQuestion> findQuestionsAndAnswers(Long spaceId, int page, int size) {

    PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.desc("id")));

    Page<Question> questions = questionRepository.findBySpaceId(spaceId, pageRequest);

    return customMapper.toResQuestions(questions);
  }

  @Transactional
  public ResQuestion updateQuestion(Long questionId, ReqQuestion req, UUID uuid) {

    userRepository.findById(uuid).orElseThrow(
      () -> new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_USER_ID));

    Question question = questionRepository.findById(questionId).orElseThrow(
      () -> new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_QUESTION_ID));

    if (!question.getUser().getId().equals(uuid)) {

      throw new BadRequestException(ErrorCode.BAD_REQUEST,
        String.format(USER_ID_NOT_MATCH, question.getUser().getId(), uuid));
    }

    if (!question.getAnswers().isEmpty()) {

      throw new BadRequestException(ErrorCode.BAD_REQUEST, ANSWERED_QUESTION_NOT_EDITED);
    }

    question.updateContent(req);

    return mapper.toResQuestion(question);
  }

  public ResAnswer updateAnswer(Long answerId, ReqAnswer req, UUID userId) {

    userRepository.findById(userId).orElseThrow(
      () -> new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_USER_ID));

    Answer answer = answerRepository.findById(answerId).orElseThrow(
      () -> new BadRequestException(ErrorCode.BAD_REQUEST, INVALID_QUESTION_ID));

    if (!answer.getUser().getId().equals(userId)) {

      throw new BadRequestException(ErrorCode.BAD_REQUEST,
        String.format(USER_ID_NOT_MATCH, answer.getUser().getId(), userId));
    }

    if (!Objects.isNull(answer.getChild())) {

      throw new BadRequestException(ErrorCode.BAD_REQUEST, ANSWERED_ANSWER_NOT_EDITED);
    }

    answer.updateContent(req);

    return mapper.toResAnswer(answer);
  }
}
