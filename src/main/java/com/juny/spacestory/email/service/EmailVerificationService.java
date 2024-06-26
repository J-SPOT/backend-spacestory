package com.juny.spacestory.email.service;

import com.juny.spacestory.email.domain.EmailVerification;
import com.juny.spacestory.email.repository.EmailVerificationCodeRepository;
import com.juny.spacestory.global.exception.ErrorCode;
import com.juny.spacestory.global.exception.hierarchy.email.EmailCodeExpiredException;
import com.juny.spacestory.global.exception.hierarchy.email.EmailCodeInvalidException;
import com.juny.spacestory.global.exception.hierarchy.parameter.ParameterIsNullOrEmpty;
import com.juny.spacestory.global.exception.hierarchy.user.UserInvalidEmailException;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

  private final EmailVerificationCodeRepository codeRepository;
  private final JavaMailSender mailSender;

  private final String EMAIL_IS_NULL_OR_EMPTY = "Email is null or empty";
  private final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
  private final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

  @Transactional
  public void sendCode(String email) {

    validateEmail(email);

    String code = UUID.randomUUID().toString();
    EmailVerification emailVerification = codeRepository.findByEmail(email).map(
        e -> {
          e.resendCode(code);
          return e;
        })
      .orElseGet(() -> new EmailVerification(email, code, LocalDateTime.now().plusMinutes(3L)));

    codeRepository.save(emailVerification);

    SimpleMailMessage message = new SimpleMailMessage();
    message.setSubject("SpaceStory Account Verification Code");
    message.setText(getText(code));
    message.setTo(email);
    mailSender.send(message);
  }

  private void validateEmail(String email) {

    if (email == null || email.trim().isEmpty()) {

      throw new ParameterIsNullOrEmpty(
        ErrorCode.PARAMETER_IS_NULL_OR_EMPTY, EMAIL_IS_NULL_OR_EMPTY);
    }

    if (!pattern.matcher(email).matches()) {

      throw new UserInvalidEmailException(ErrorCode.USER_INVALID_EMAIL);
    }
  }

  private String getText(String code) {
    StringBuilder sb = new StringBuilder();

    String encodedCode = new String(Base64.getEncoder().encode(code.getBytes()));
    sb.append("Verification Code: ").append(encodedCode);

    return sb.toString();
  }

  @Transactional
  public void verifyCode(String email, String code) {

    validateEmail(email);

    String actualCode = new String(Base64.getDecoder().decode(code.getBytes()));

    EmailVerification emailVerification = codeRepository.findByEmail(email).orElseThrow(
      () -> new UserInvalidEmailException(ErrorCode.USER_INVALID_EMAIL)
    );

    if (!emailVerification.getCode().equals(actualCode)) {
      throw new EmailCodeInvalidException(ErrorCode.EMAIL_CODE_INVALID);
    }

    if (emailVerification.isExpired()) {
      throw new EmailCodeExpiredException(ErrorCode.EMAIL_CODE_EXPIRED);
    }

    emailVerification.verifyCode();
  }
}
