package com.juny.spacestory.space.domain.hashtag;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {

  Optional<Hashtag> findByName(String name);
}
