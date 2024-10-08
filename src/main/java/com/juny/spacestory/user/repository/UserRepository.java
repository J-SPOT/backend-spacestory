package com.juny.spacestory.user.repository;

import com.juny.spacestory.user.domain.User;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, UUID> {

  Boolean existsByEmail(String email);

  Optional<User> findByEmail(String email);

  Optional<User> findByEmailAndSocialIdNot(String email, String socialId);

  Optional<User> findBySocialId(String socialId);

  Optional<User> findByIdAndDeletedAtIsNull(UUID id);
}
