package com.arquisoft.shared.exception;

public class AuthorizationException extends BaseException {

    public AuthorizationException(BaseError error) {
        super(error);
    }

    public AuthorizationException(BaseError error, Throwable cause) {
        super(error, cause);
    }

    public AuthorizationException(String message, String errorCode) {
        super(BaseError.of(errorCode, message));
    }

    public AuthorizationException(String message, String errorCode, Throwable cause) {
        super(BaseError.of(errorCode, message, cause), cause);
    }
}
