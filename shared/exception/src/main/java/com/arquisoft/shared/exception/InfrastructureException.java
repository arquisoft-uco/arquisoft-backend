package com.arquisoft.shared.exception;

public class InfrastructureException extends BaseException {

    public InfrastructureException(BaseError error) {
        super(error);
    }

    public InfrastructureException(BaseError error, Throwable cause) {
        super(error, cause);
    }

    public InfrastructureException(String message, String errorCode) {
        super(BaseError.of(errorCode, message));
    }

    public InfrastructureException(String message, String errorCode, Throwable cause) {
        super(BaseError.of(errorCode, message, cause), cause);
    }
}
