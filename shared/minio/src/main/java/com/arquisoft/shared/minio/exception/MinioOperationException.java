package com.arquisoft.shared.minio.exception;

public final class MinioOperationException extends RuntimeException {

    public MinioOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
