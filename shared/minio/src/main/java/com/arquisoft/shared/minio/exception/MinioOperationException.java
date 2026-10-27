package com.arquisoft.shared.minio.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.AppCodes;
import com.arquisoft.shared.message.key.app.AlmacenamientoKey;

public final class MinioOperationException extends InfrastructureException {

    public MinioOperationException(String message, String errorCode, Throwable cause) {
        super(message, errorCode, cause);
    }

    private MinioOperationException(BaseError error, Throwable cause) {
        super(error, cause);
    }

    public static MinioOperationException lecturaFallida(Throwable cause) {
        return new MinioOperationException(BaseError.conDetalle(
                AppCodes.Minio.LECTURA_FALLIDA,
                Mensajes.obtener(AlmacenamientoKey.ERROR_LECTURA),
                Mensajes.obtener(AlmacenamientoKey.ERROR_LECTURA_DETALLE)), cause);
    }
}
