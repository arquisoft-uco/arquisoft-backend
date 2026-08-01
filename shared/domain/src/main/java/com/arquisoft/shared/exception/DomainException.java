package com.arquisoft.shared.exception;

public class DomainException extends BaseException {

    public DomainException(BaseError error) {
        super(error);
    }

    public DomainException(BaseError error, Throwable cause) {
        super(error, cause);
    }

    // Constructores de compatibilidad para código existente
    public DomainException(String message, String errorCode) {
        super(BaseError.of(errorCode, message));
    }

    public DomainException(String message, String errorCode, Throwable cause) {
        super(BaseError.of(errorCode, message, cause), cause);
    }
}
