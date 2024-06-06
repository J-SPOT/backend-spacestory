package com.juny.spacestory.user.service;

import com.juny.spacestory.global.exception.ErrorCode;
import com.juny.spacestory.global.exception.hierarchy.user.UserDuplicatedEmailException;
import com.juny.spacestory.global.exception.hierarchy.user.UserNotMatchPasswordException;
import com.juny.spacestory.global.exception.hierarchy.user.UserPasswordTooShortException;
import com.juny.spacestory.global.exception.hierarchy.parameter.ParameterIsNullOrEmpty;
import com.juny.spacestory.user.domain.User;
import com.juny.spacestory.user.dto.ReqRegisterUser;
import com.juny.spacestory.user.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {

    this.userRepository = userRepository;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
  }

  public void register(ReqRegisterUser req) {

    validateParams(req);

    User user = new User(req.name(), req.email(), bCryptPasswordEncoder.encode(req.password()));

    userRepository.save(user);
  }

  private void validateParams(ReqRegisterUser req) {

    if (req.name() == null || req.name().trim().isEmpty()) {

      throw new ParameterIsNullOrEmpty(
          ErrorCode.PARAMETER_IS_NULL_OR_EMPTY, "register, req.name() is null or empty");
    }

    if (req.email() == null || req.email().trim().isEmpty()) {

      throw new ParameterIsNullOrEmpty(
          ErrorCode.PARAMETER_IS_NULL_OR_EMPTY, "register, req.email() is null or empty");
    }

    if (req.password() == null || req.password().trim().isEmpty()) {

      throw new ParameterIsNullOrEmpty(
          ErrorCode.PARAMETER_IS_NULL_OR_EMPTY, "register, req.password() is null or empty");
    }

    if (req.passwordCheck() == null || req.passwordCheck().trim().isEmpty()) {

      throw new ParameterIsNullOrEmpty(
          ErrorCode.PARAMETER_IS_NULL_OR_EMPTY, "register, req.passwordCheck() is null or empty");
    }

    if (userRepository.existsByEmail(req.email())) {

      throw new UserDuplicatedEmailException(ErrorCode.UserDuplicatedEmail);
    }

    if (!req.password().equals(req.passwordCheck())) {

      throw new UserNotMatchPasswordException(ErrorCode.UserNotMatchPassword);
    }

    if (req.password().length() < 4) {

      throw new UserPasswordTooShortException(ErrorCode.UserPasswordIsShort);
    }
  }
}
