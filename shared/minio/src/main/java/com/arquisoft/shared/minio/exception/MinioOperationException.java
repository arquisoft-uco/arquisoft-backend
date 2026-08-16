package com.arquisoft.shared.minio.exception;

import com.arquisoft.shared.exception.InfrastructureException;

public final class MinioOperationException extends InfrastructureException {

    public MinioOperationException(String message, String errorCode, Throwable cause) {
        super(message, errorCode, cause);
    }
}
