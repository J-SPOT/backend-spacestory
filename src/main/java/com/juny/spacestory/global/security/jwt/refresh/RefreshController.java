package com.juny.spacestory.global.security.jwt.refresh;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RefreshController {

  private final RefreshService refreshService;

  public RefreshController(RefreshService refreshService) {
    this.refreshService = refreshService;
  }

  @PostMapping("/api/v1/auth/tokens")
  public ResponseEntity<ResReissueTokens> reissue(@RequestBody ReqReissueTokens req) {

    ResReissueTokens resReissueTokens = refreshService.reissue(req.refreshToken());

    return new ResponseEntity<>(resReissueTokens, HttpStatus.OK);
  }
}
