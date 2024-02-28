package com.juny.spacestory.exception.space;

import com.juny.spacestory.exception.InvalidFormatException;
import com.juny.spacestory.exception.global.ErrorCode;

public class SpaceInvalidDetailedTypeException extends InvalidFormatException {
    public SpaceInvalidDetailedTypeException(ErrorCode errorCode) {
        super(ErrorCode.SPACE_INVALID_DETAILED_TYPE);
    }
}
