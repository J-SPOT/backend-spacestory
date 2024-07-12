package com.juny.spacestory.space.domain.hashtag;

import java.util.List;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface HashtagMapper {

  ResHashtag toResHashtag(Hashtag hashtag);

  default Page<ResHashtag> toResHashtags(Page<Hashtag> hashtags) {
    return hashtags.map(this::toResHashtag);
  }
}
