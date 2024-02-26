package com.juny.spacestory.exception.space;

import com.juny.spacestory.exception.global.ErrorCode;
import com.juny.spacestory.exception.InvalidFormatException;

public class SpaceInvalidIdException extends InvalidFormatException {
    public SpaceInvalidIdException(ErrorCode errorCode) {
        super(ErrorCode.SPACE_INVALID_ID);
    }
}
