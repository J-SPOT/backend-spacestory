package com.juny.spacestory.email.repository;

import com.juny.spacestory.email.domain.EmailVerification;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailVerificationCodeRepository extends JpaRepository<EmailVerification, Long> {

  Optional<EmailVerification> findByEmail(String email);

  Optional<EmailVerification> findByEmailAndIsVerifiedFalse(String email);
}
