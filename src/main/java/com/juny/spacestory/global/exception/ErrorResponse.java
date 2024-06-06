package com.juny.spacestory.global.exception;

import lombok.Getter;

@Getter
public class ErrorResponse {
  private final String code;
  private final String msg;

  public ErrorResponse(ErrorCode errorCode) {
    this.code = errorCode.getCode();
    this.msg = errorCode.getMsg();
  }

  public ErrorResponse(String code, String msg) {
    this.code = code;
    this.msg = msg;
  }
}
