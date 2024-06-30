package com.juny.spacestory.user.service;

import com.juny.spacestory.user.repository.EmailVerificationCodeRepository;
import com.juny.spacestory.global.exception.ErrorCode;
import com.juny.spacestory.global.exception.common.BadRequestException;
import com.juny.spacestory.global.exception.hierarchy.user.UserDuplicatedEmailException;
import com.juny.spacestory.global.exception.hierarchy.user.UserInvalidEmailException;
import com.juny.spacestory.global.exception.hierarchy.user.UserNotMatchPasswordException;
import com.juny.spacestory.global.exception.hierarchy.user.UserPasswordTooShortException;
import com.juny.spacestory.global.exception.hierarchy.parameter.ParameterIsNullOrEmpty;
import com.juny.spacestory.user.domain.User;
import com.juny.spacestory.user.dto.ReqRegisterUser;
import com.juny.spacestory.user.repository.UserRepository;
import java.util.List;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final EmailVerificationCodeRepository codeRepository;
  private final GoogleRecaptchaService recaptchaService;

  private final String NAME_IS_NULL_OR_EMPTY = "Name is null or empty";
  private final String EMAIL_IS_NULL_OR_EMPTY = "Email is null or empty";
  private final String PASSWORD_IS_NULL_OR_EMPTY = "Password is null or empty";
  private final String PASSWORD_CHECK_IS_NULL_OR_EMPTY = "PasswordCheck is null or empty";
  private final String RECAPTCHA_IS_INVALID = "Recaptcha is invalid";
  private final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
  private final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

  public void register(ReqRegisterUser req, String remoteAddr) {

    validateParams(req);

    User user = new User(req.name(), req.email(), bCryptPasswordEncoder.encode(req.password()), remoteAddr);

    userRepository.save(user);
  }

  private void validateParams(ReqRegisterUser req) {

    if (req.name() == null || req.name().trim().isEmpty()) {

      throw new ParameterIsNullOrEmpty(ErrorCode.PARAMETER_IS_NULL_OR_EMPTY, NAME_IS_NULL_OR_EMPTY);
    }

    if (req.email() == null || req.email().trim().isEmpty()) {

      throw new ParameterIsNullOrEmpty(
          ErrorCode.PARAMETER_IS_NULL_OR_EMPTY, EMAIL_IS_NULL_OR_EMPTY);
    }

    if (req.password() == null || req.password().trim().isEmpty()) {

      throw new ParameterIsNullOrEmpty(
          ErrorCode.PARAMETER_IS_NULL_OR_EMPTY, PASSWORD_IS_NULL_OR_EMPTY);
    }

    if (req.passwordCheck() == null || req.passwordCheck().trim().isEmpty()) {

      throw new ParameterIsNullOrEmpty(
          ErrorCode.PARAMETER_IS_NULL_OR_EMPTY, PASSWORD_CHECK_IS_NULL_OR_EMPTY);
    }

    if (!pattern.matcher(req.email()).matches()) {

      throw new UserInvalidEmailException(ErrorCode.USER_INVALID_EMAIL);
    }

    if (userRepository.existsByEmail(req.email())) {

      throw new UserDuplicatedEmailException(ErrorCode.USER_DUPLICATED_EMAIL);
    }

    if (!req.password().equals(req.passwordCheck())) {

      throw new UserNotMatchPasswordException(ErrorCode.USER_NOT_MATCH_PASSWORD);
    }

    if (req.password().length() < 4) {

      throw new UserPasswordTooShortException(ErrorCode.UserPasswordIsShort);
    }

    codeRepository.findByEmail(req.email()).orElseThrow(
      () -> new BadRequestException(ErrorCode.EMAIL_CODE_INVALID));

    if (!recaptchaService.verifyRecaptcha(req.captchaToken())) {
      throw new BadRequestException(ErrorCode.BAD_REQUEST, RECAPTCHA_IS_INVALID);
    }
  }

  public void addIpAddress(User user, String ipAddress) {

    List<String> ipAddresses = user.getIpAddresses();
    if (ipAddresses.contains(ipAddress)) {
      return;
    }

    ipAddresses.add(ipAddress);

    if (user.getIpAddresses().size() > 20) {
      user.getIpAddresses().remove(0);
    }

    userRepository.save(user);
  }
}
