package com.juny.spacestory.exception.global;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "E1", "Invalid request format. Please check the request data."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "E2", "The requested resource could not be found. Please ensure the URL is correct."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "E3", "You do not have permission to perform this request. Please check your access permissions."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "E4", "Unsupported HTTP method. Please check the request method."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E5", "Internal server error occurred."),

    RESERVATION_INVALID_ID(HttpStatus.BAD_REQUEST, "R1", "The reservation requested is invalid. Please review your request."),
    RESERVATION_MINIMUM_TIME(HttpStatus.BAD_REQUEST, "R2", "The minimum booking duration must be at least 1 hour. Please confirm the reservation time."),
    RESERVATION_OVERLAPPED_TIME(HttpStatus.BAD_REQUEST, "R3", "There is a scheduling conflict. Please verify the reservation time."),

    SPACE_INVALID_ID(HttpStatus.BAD_REQUEST, "S1", "The space requested is invalid. Please review your request."),
    SPACE_INVALID_DETAILED_TYPE(HttpStatus.BAD_REQUEST, "S2", "The space detailed type is invalid. please review your request."),

    HOST_INVALID_ID(HttpStatus.BAD_REQUEST, "H1", "The Host requested is invalid. Please review your request."),

    REVIEW_INVALID_ID(HttpStatus.BAD_REQUEST, "H1", "The review requested is invalid. Please review your request."),

    USER_INVALID_ID(HttpStatus.BAD_REQUEST, "U1", "The user requested is invalid. Please review your request."),
    USER_NOT_ENOUGH_POINT(HttpStatus.BAD_REQUEST, "U2", "The User's point exceeded limit. Please check your point."),
    USER_UNAUTHORIZED_TO_MODIFY(HttpStatus.BAD_REQUEST, "U3", "The user does not have permission to modify. Please verify permissions.");

    private final HttpStatus status;
    private final String code;
    private final String msg;

    ErrorCode(HttpStatus status, String code, String msg) {
        this.status = status;
        this.code = code;
        this.msg = msg;
    }
}
