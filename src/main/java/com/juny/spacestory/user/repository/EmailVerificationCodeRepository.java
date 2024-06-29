package com.juny.spacestory.user.repository;

import com.juny.spacestory.user.domain.EmailVerification;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailVerificationCodeRepository extends JpaRepository<EmailVerification, Long> {

  Optional<EmailVerification> findByEmail(String email);
}
