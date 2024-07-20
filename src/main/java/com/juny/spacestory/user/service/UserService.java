package com.juny.spacestory.user.service;

import com.juny.spacestory.global.exception.hierarchy.user.UserInvalidPhoneNumberException;
import com.juny.spacestory.user.dto.ReqModifyPassword;
import com.juny.spacestory.user.dto.ReqModifyProfile;
import com.juny.spacestory.user.dto.ResLookUpUser;
import com.juny.spacestory.user.dto.ResLookUpUsers;
import com.juny.spacestory.user.dto.ResModifyUser;
import com.juny.spacestory.user.mapper.UserMapper;
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
import com.juny.spacestory.user.repository.TotpVerificationCodeRepository;
import com.juny.spacestory.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final EmailVerificationCodeRepository codeRepository;
  private final GoogleRecaptchaService recaptchaService;
  private final UserMapper mapper;
  private final BCryptPasswordEncoder passwordEncoder;

  private final String NAME_IS_NULL_OR_EMPTY = "Name is null or empty";
  private final String EMAIL_IS_NULL_OR_EMPTY = "Email is null or empty";
  private final String PASSWORD_IS_NULL_OR_EMPTY = "Password is null or empty";
  private final String PHONE_NUMBER_IS_NULL_OR_EMPTY = "PhoneNumber is null or empty";
  private final String PASSWORD_CHECK_IS_NULL_OR_EMPTY = "PasswordCheck is null or empty";
  private final String RECAPTCHA_IS_INVALID = "Recaptcha is invalid";
  private final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
  private final String PHONE_NUMBER_PATTERN = "^010-\\d{4}-\\d{4}$";
  private final String NON_EXISTENT_USER = "User not found";
  private final Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);
  private final Pattern phoneNumberPattern = Pattern.compile(PHONE_NUMBER_PATTERN);
  private final TotpVerificationCodeRepository totpVerificationCodeRepository;

  public void register(ReqRegisterUser req, String ip) {

    validateParams(req);

    User user = new User(req.name(), req.email(), bCryptPasswordEncoder.encode(req.password()), ip);

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

    if (!emailPattern.matcher(req.email()).matches()) {

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

  public ResLookUpUser lookUpPrivacy(UUID uuid) {
    User user = userRepository.findById(uuid).orElseThrow(
      () -> new BadRequestException(ErrorCode.BAD_REQUEST, NON_EXISTENT_USER));

    return mapper.toResLookUpUser(user);
  }

  public Page<ResLookUpUsers> lookUpUsers(int page, int size) {

    Page<User> users = userRepository.findAll(PageRequest.of(page, size));

    return mapper.toResLookUpUsers(users);
  }

  @Transactional
  public ResModifyUser modifyPrivacy(ReqModifyProfile req, UUID uuid) {

    validateProfile(req);

    User user = userRepository.findById(uuid).orElseThrow(
      () -> new BadRequestException(ErrorCode.BAD_REQUEST, NON_EXISTENT_USER));

    if (!req.email().equals(user.getEmail())) {
      user.setTotpDisable();
      totpVerificationCodeRepository.deleteByEmail(user.getEmail());
    }

    user.modifyUser(req);

    return mapper.toResModifyUser(user);
  }

  private void validateProfile(ReqModifyProfile req) {
    if (req.name() == null || req.name().trim().isEmpty()) {

      throw new ParameterIsNullOrEmpty(ErrorCode.PARAMETER_IS_NULL_OR_EMPTY, NAME_IS_NULL_OR_EMPTY);
    }

    if (req.email() == null || req.email().trim().isEmpty()) {

      throw new ParameterIsNullOrEmpty(
        ErrorCode.PARAMETER_IS_NULL_OR_EMPTY, EMAIL_IS_NULL_OR_EMPTY);
    }

    if (req.phoneNumber() == null || req.phoneNumber().trim().isEmpty()) {

      throw new ParameterIsNullOrEmpty(
        ErrorCode.PARAMETER_IS_NULL_OR_EMPTY, PHONE_NUMBER_IS_NULL_OR_EMPTY);
    }

    if (!emailPattern.matcher(req.email()).matches()) {

      throw new UserInvalidEmailException(ErrorCode.USER_INVALID_EMAIL);
    }

    if (!phoneNumberPattern.matcher(req.phoneNumber()).matches()) {

      throw new UserInvalidPhoneNumberException(ErrorCode.USER_INVALID_PHONE_NUMBER);
    }

    if (userRepository.existsByEmail(req.email())) {

      throw new UserDuplicatedEmailException(ErrorCode.USER_DUPLICATED_EMAIL);
    }
  }

  @Transactional
  public void modifyPassword(ReqModifyPassword req, UUID uuid) {

    validatePassword(req);

    User user = userRepository.findById(uuid).orElseThrow(
      () -> new BadRequestException(ErrorCode.BAD_REQUEST, NON_EXISTENT_USER));

    if (!passwordEncoder.matches(req.oldPassword(), user.getPassword())) {

      throw new UserNotMatchPasswordException(ErrorCode.USER_NOT_MATCH_PASSWORD);
    }

    user.changePassword(passwordEncoder.encode(req.newPassword()));
  }

  private void validatePassword(ReqModifyPassword req) {

    if (req.oldPassword() == null || req.oldPassword().trim().isEmpty()) {

      throw new ParameterIsNullOrEmpty(ErrorCode.PARAMETER_IS_NULL_OR_EMPTY, PASSWORD_IS_NULL_OR_EMPTY);
    }

    if (req.newPassword().length() < 4) {

      throw new UserPasswordTooShortException(ErrorCode.UserPasswordIsShort);
    }
  }

  public void disableTotp(String uuid) {

    User user = userRepository.findById(UUID.fromString(uuid)).orElseThrow(
      () -> new BadRequestException(ErrorCode.BAD_REQUEST, NON_EXISTENT_USER));

    user.setTotpDisable();
  }

  @Transactional
  public void delete(UUID uuid) {

    User user = userRepository.findById(uuid).orElseThrow(
      () -> new BadRequestException(ErrorCode.BAD_REQUEST, NON_EXISTENT_USER));

    user.softDelete();
  }
}
