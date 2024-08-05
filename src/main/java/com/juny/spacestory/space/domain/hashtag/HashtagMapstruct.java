package com.juny.spacestory.space.domain.hashtag;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface HashtagMapstruct {

  ResHashtag toResHashtag(Hashtag hashtag);

  default Page<ResHashtag> toResHashtags(Page<Hashtag> hashtags) {
    return hashtags.map(this::toResHashtag);
  }
}
