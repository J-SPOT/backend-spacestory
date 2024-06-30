package com.juny.spacestory.user.repository;

import com.juny.spacestory.user.domain.TotpVerification;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TotpVerificationCodeRepository extends JpaRepository<TotpVerification, Long> {

  void deleteByEmail(String email);

  Optional<TotpVerification> findByEmail(String email);
}
