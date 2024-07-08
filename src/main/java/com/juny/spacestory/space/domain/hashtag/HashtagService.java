package com.juny.spacestory.space.domain.hashtag;

import com.juny.spacestory.global.exception.ErrorCode;
import com.juny.spacestory.global.exception.common.BadRequestException;
import com.juny.spacestory.space.repository.SpaceRepository;
import jakarta.transaction.Transactional;
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

  private final String InvalidHashtagMsg = "Hashtag Not found";

  public List<ResHashtag> findHashtags() {

    List<Hashtag> hashtags = hashtagRepository.findAll(
      Sort.by(Order.asc("id")));

    return mapper.toResHashtags(hashtags);
  }

  public ResHashtag findHashtagById(Long id) {

    Hashtag hashtag = hashtagRepository.findById(id).orElseThrow(
      () -> new BadRequestException(ErrorCode.BAD_REQUEST, InvalidHashtagMsg));

    return mapper.toResHashtag(hashtag);
  }

  public ResHashtag findHashtagByName(String name) {

    Hashtag hashtag = hashtagRepository.findByName(name).orElseThrow(
      () -> new BadRequestException(ErrorCode.BAD_REQUEST, InvalidHashtagMsg));

    return mapper.toResHashtag(hashtag);
  }

  public ResHashtag createHashtag(String name) {

    Hashtag savedHashtag = hashtagRepository.save(new Hashtag(name));

    return mapper.toResHashtag(savedHashtag);
  }

  public void deleteHashtagById(Long id) {

    hashtagRepository.deleteById(id);
  }

  public void deleteHashtagByName(String name) {

    hashtagRepository.deleteByName(name);
  }

}
