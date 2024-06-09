package com.juny.spacestory.global.exception;

import com.juny.spacestory.global.exception.common.CustomBusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
  @ExceptionHandler({CustomBusinessException.class})
  public ResponseEntity<ErrorResponse> handle(CustomBusinessException e) {

    return new ResponseEntity<>(
        new ErrorResponse(e.getErrorCode().getCode(), e.getMessage()),
        e.getErrorCode().getStatus());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handle(Exception e) {

    e.printStackTrace();
    log.error("global exception {}", e.getMessage());
    return new ResponseEntity<>(
        new ErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR.getCode(), e.getMessage()),
        ErrorCode.INTERNAL_SERVER_ERROR.getStatus());
  }
}
