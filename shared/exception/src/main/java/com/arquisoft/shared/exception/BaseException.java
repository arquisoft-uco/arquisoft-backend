package com.arquisoft.shared.exception;

public abstract class BaseException extends RuntimeException {

    private final BaseError error;

    protected BaseException(BaseError error) {
        super(error.getDetalle());
        this.error = error;
    }

    protected BaseException(BaseError error, Throwable cause) {
        super(error.getDetalle(), cause);
        this.error = error;
    }

    public BaseError getError() {
        return error;
    }

    public String getCodigoError() {
        return error.getCodigoError();
    }
}
