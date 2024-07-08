package com.juny.spacestory.space.domain.hashtag;

import com.juny.spacestory.global.exception.ErrorCode;
import com.juny.spacestory.global.exception.common.BadRequestException;
import com.juny.spacestory.space.domain.Space;
import com.juny.spacestory.space.repository.SpaceRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HashtagService {

  private final HashtagRepository hashtagRepository;
  private final SpaceRepository spaceRepository;
  private final HashtagMapper mapper;

  public List<ResHashtag> findHashtags() {

    List<Hashtag> hashtags = hashtagRepository.findAll(
      Sort.by(Order.asc("id")));

    return mapper.toResHashtags(hashtags);
  }

  public ResHashtag findHashtagById(Long id) {

    Hashtag hashtag = hashtagRepository.findById(id).orElseThrow(
      () -> new BadRequestException(ErrorCode.BAD_REQUEST, "Hashtag not found"));

    return mapper.toResHashtag(hashtag);
  }

  public ResHashtag findHashTagByName(String name) {

    Hashtag hashtag = hashtagRepository.findByName(name).orElseThrow(
      () -> new BadRequestException(ErrorCode.BAD_REQUEST, "Hashtag not found"));

    return mapper.toResHashtag(hashtag);
  }

  public ResHashtag createHashtag(Long spaceId, String name) {

    Space space = spaceRepository.findById(spaceId).orElseThrow(
      () -> new BadRequestException(ErrorCode.BAD_REQUEST, "Space not found"));

    Hashtag hashtag = hashtagRepository.findByName(name).orElseGet(() -> new Hashtag(name));

    space.addHashtag(hashtag);

    return null;
  }
}
