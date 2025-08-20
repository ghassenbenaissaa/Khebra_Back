package com.example.Khebra.exception;

import com.example.Khebra.handler.BusinessErrorCodes;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final BusinessErrorCodes errorCode;

    public BusinessException(BusinessErrorCodes errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
    }
}
