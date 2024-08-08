package com.juny.spacestory.space.domain.hashtag;

import com.juny.spacestory.global.exception.ErrorCode;
import com.juny.spacestory.global.exception.common.BadRequestException;
import java.util.Optional;
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
  private final HashtagMapstruct mapstruct;

  private final String InvalidHashtagMsg = "Hashtag Not found";

  public Page<ResHashtag> findHashtags(int page, int size) {

    PageRequest pageable = PageRequest.of(page, size, Sort.by(Order.asc("id")));

    Page<Hashtag> hashtags = hashtagRepository.findAll(pageable);

    return mapstruct.toResHashtags(hashtags);
  }

  public ResHashtag findHashtagById(Long id) {

    Hashtag hashtag = hashtagRepository.findById(id).orElseThrow(
      () -> new BadRequestException(ErrorCode.BAD_REQUEST, InvalidHashtagMsg));

    return mapstruct.toResHashtag(hashtag);
  }

  public ResHashtag findHashtagByName(String name) {

    Hashtag hashtag = hashtagRepository.findByName(name).orElseThrow(
      () -> new BadRequestException(ErrorCode.BAD_REQUEST, InvalidHashtagMsg));

    return mapstruct.toResHashtag(hashtag);
  }

  public ResHashtag createHashtag(String name) {

    Optional<Hashtag> hashtag = hashtagRepository.findByName(name);

    Hashtag savedhashtag = hashtag.orElseGet(() -> {
      Hashtag newHashtag = new Hashtag(name);
      return hashtagRepository.save(newHashtag);
    });

    return mapstruct.toResHashtag(savedhashtag);
  }

  public void deleteHashtagById(Long id) {

    hashtagRepository.deleteById(id);
  }

  public void deleteHashtagByName(String name) {

    hashtagRepository.deleteByName(name);
  }

}
