package com.juny.spacestory.space.domain.option;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OptionRepository extends JpaRepository<Option, Long> {

  Optional<Option> findByName(String name);
}
