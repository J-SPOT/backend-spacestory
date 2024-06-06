package com.juny.spacestory.global.exception;

import com.juny.spacestory.global.exception.common.constant.Const;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
  INTERNAL_SERVER_ERROR(
      HttpStatus.INTERNAL_SERVER_ERROR, Const.SERVER_ERROR_CODE, Const.SERVER_ERROR_MSG),

  BAD_REQUEST(HttpStatus.BAD_REQUEST, Const.BAD_REQUEST_CODE, Const.BAD_REQUEST_MSG),

  UNAUTHORIZED(HttpStatus.UNAUTHORIZED, Const.UNAUTHORIZED_CODE, Const.UNAUTHORIZED_MSG),

  PARAMETER_IS_NULL_OR_EMPTY(
      HttpStatus.BAD_REQUEST,
      Const.PARAMETER_NULL_OR_EMPTY_CODE,
      Const.PARAMETER_NULL_OR_EMPTY_MSG),

  RESERVATION_INVALID_ID(
      HttpStatus.BAD_REQUEST, Const.RESERVATION_INVALID_ID_CODE, Const.RESERVATION_INVALID_ID_MSG),

  RESERVATION_MINIMUM_TIME(
      HttpStatus.BAD_REQUEST,
      Const.RESERVATION_MINIMUM_TIME_CODE,
      Const.RESERVATION_MINIMUM_TIME_MSG),

  RESERVATION_OVERLAPPED_TIME(
      HttpStatus.BAD_REQUEST,
      Const.RESERVATION_OVERLAPPED_TIME_CODE,
      Const.RESERVATION_OVERLAPPED_TIME_MSG),

  SPACE_INVALID_ID(HttpStatus.BAD_REQUEST, Const.SPACE_INVALID_ID_CODE, Const.SPACE_INVALID_ID_MSG),

  SPACE_INVALID_DETAILED_TYPE(
      HttpStatus.BAD_REQUEST,
      Const.SPACE_INVALID_DETAILED_TYPE_CODE,
      Const.SPACE_INVALID_DETAILED_TYPE_MSG),

  HOST_INVALID_ID(HttpStatus.BAD_REQUEST, Const.HOST_INVALID_ID_CODE, Const.HOST_INVALID_ID_MSG),

  REVIEW_INVALID_ID(
      HttpStatus.BAD_REQUEST, Const.REVIEW_INVALID_ID_CODE, Const.REVIEW_INVALID_ID_MSG),

  USER_INVALID_ID(HttpStatus.BAD_REQUEST, Const.USER_INVALID_ID_CODE, Const.USER_INVALID_ID_MSG),
  USER_NOT_ENOUGH_POINT(
      HttpStatus.BAD_REQUEST, Const.USER_NOT_ENOUGH_POINTS_CODE, Const.USER_NOT_ENOUGH_POINTS_MSG),
  USER_UNAUTHORIZED_TO_MODIFY(
      HttpStatus.BAD_REQUEST,
      Const.USER_UNAUTHORIZED_MODIFY_CODE,
      Const.USER_UNAUTHORIZED_MODIFY_MSG),
  UserDuplicatedEmail(
      HttpStatus.BAD_REQUEST, Const.USER_DUPLICATED_EMAIL_CODE, Const.USER_DUPLICATED_EMAIL_MSG),
  UserNotMatchPassword(
      HttpStatus.BAD_REQUEST,
      Const.USER_NOT_MATCH_PASSWORD_CODE,
      Const.USER_NOT_MATCH_PASSWORD_MSG),

  UserPasswordIsShort(
      HttpStatus.BAD_REQUEST, Const.USER_PASSWORD_IS_SHORT_CODE, Const.USER_PASSWORD_IS_SHORT_MSG),

  TOKEN_REFRESH_INVALID(
      HttpStatus.BAD_REQUEST, Const.TOKEN_REFRESH_INVALID_CODE, Const.TOKEN_REFRESH_INVALID_MSG),
  ;

  private final HttpStatus status;
  private final String code;
  private final String msg;

  ErrorCode(HttpStatus status, String code, String msg) {
    this.status = status;
    this.code = code;
    this.msg = msg;
  }
}
