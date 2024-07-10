package com.juny.spacestory.space.domain.hashtag;

import com.juny.spacestory.global.exception.ErrorCode;
import com.juny.spacestory.global.exception.common.BadRequestException;
import com.juny.spacestory.space.repository.SpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

  public Page<ResHashtag> findHashtags(int page, int size) {

    PageRequest pageable = PageRequest.of(page, size, Sort.by(Order.asc("id")));

    Page<Hashtag> hashtags = hashtagRepository.findAll(pageable);

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
