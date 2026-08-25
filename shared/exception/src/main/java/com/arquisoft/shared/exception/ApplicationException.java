package com.arquisoft.shared.exception;

public class ApplicationException extends BaseException {

    public ApplicationException(BaseError error) {
        super(error);
    }

    public ApplicationException(BaseError error, Throwable cause) {
        super(error, cause);
    }

    public ApplicationException(String message, String errorCode) {
        super(BaseError.of(errorCode, message));
    }

    public ApplicationException(String message, String errorCode, Throwable cause) {
        super(BaseError.of(errorCode, message, cause), cause);
    }
}
