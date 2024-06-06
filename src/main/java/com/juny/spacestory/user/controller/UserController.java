package com.juny.spacestory.user.controller;

import com.juny.spacestory.user.service.UserService;
import com.juny.spacestory.user.dto.ReqRegisterUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {

    this.userService = userService;
  }

  @PostMapping("/api/v1/auth/register")
  public ResponseEntity<Void> register(@RequestBody ReqRegisterUser req) {
    userService.register(req);

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
