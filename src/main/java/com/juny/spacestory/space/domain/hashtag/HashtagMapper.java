package com.juny.spacestory.space.domain.hashtag;

import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface HashtagMapper {

  ResHashtag toResHashtag(Hashtag hashtag);

  List<ResHashtag> toResHashtags(List<Hashtag> hashtags);
}
