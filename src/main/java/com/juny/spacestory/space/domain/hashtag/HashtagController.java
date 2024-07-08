package com.juny.spacestory.space.domain.hashtag;

import com.juny.spacestory.space.domain.option.ReqOption;
import com.juny.spacestory.space.domain.option.ResOption;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HashtagController {

  private final HashtagService hashTagService;

  @GetMapping("/api/v1/hashtags/search")
  public ResponseEntity<?> SearchHashtag(
    @RequestParam(required = false) Long id,
    @RequestParam(required = false) String name) {

    if (id == null && name == null) {
      List<ResHashtag> hashtags = hashTagService.findHashtags();
      return new ResponseEntity<>(hashtags, HttpStatus.OK);
    }
    if (id != null) {
      ResHashtag hashtag = hashTagService.findHashtagById(id);
      return new ResponseEntity<>(hashtag, HttpStatus.OK);
    }
    ResHashtag hashtag = hashTagService.findHashTagByName(name);
    return new ResponseEntity<>(hashtag, HttpStatus.OK);
  }

  @PostMapping("/api/v1/hashtags")
  public ResponseEntity<ResHashtag> createHashtag(@RequestBody ReqHashtag req) {

    ResHashtag hashtag = hashTagService.createHashtag(req.spaceId(), req.name());

    return null;
  }
//  @PatchMapping("/api/v1/hashtags/{id}")
//  @DeleteMapping("/api/v1/hashtags/{id}")

}
