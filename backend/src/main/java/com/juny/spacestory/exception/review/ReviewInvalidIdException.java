package com.juny.spacestory.exception.review;

import com.juny.spacestory.exception.InvalidFormatException;
import com.juny.spacestory.exception.global.ErrorCode;

public class ReviewInvalidIdException extends InvalidFormatException {
    public ReviewInvalidIdException(ErrorCode errorCode) {
        super(ErrorCode.REVIEW_INVALID_ID);
    }
}
