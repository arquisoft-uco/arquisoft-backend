package com.arquisoft.shared.minio.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.AppCodes;
import com.arquisoft.shared.message.key.app.AlmacenamientoKey;

public final class MinioOperationException extends InfrastructureException {

    private MinioOperationException(BaseError error, Throwable cause) {
        super(error, cause);
    }

    public static MinioOperationException formularioCargaFallido(Throwable cause) {
        return crear(AppCodes.Minio.FORMULARIO_CARGA_FALLIDO, AlmacenamientoKey.ERROR_FORMULARIO_CARGA,
                AlmacenamientoKey.ERROR_FORMULARIO_CARGA_DETALLE, cause);
    }

    public static MinioOperationException consultaFallida(Throwable cause) {
        return crear(AppCodes.Minio.CONSULTA_FALLIDA, AlmacenamientoKey.ERROR_CONSULTA,
                AlmacenamientoKey.ERROR_CONSULTA_DETALLE, cause);
    }

    public static MinioOperationException lecturaFallida(Throwable cause) {
        return crear(AppCodes.Minio.LECTURA_FALLIDA, AlmacenamientoKey.ERROR_LECTURA,
                AlmacenamientoKey.ERROR_LECTURA_DETALLE, cause);
    }

    public static MinioOperationException copiaFallida(Throwable cause) {
        return crear(AppCodes.Minio.COPIA_FALLIDA, AlmacenamientoKey.ERROR_COPIA,
                AlmacenamientoKey.ERROR_COPIA_DETALLE, cause);
    }

    public static MinioOperationException eliminacionFallida(Throwable cause) {
        return crear(AppCodes.Minio.ELIMINACION_FALLIDA, AlmacenamientoKey.ERROR_ELIMINACION,
                AlmacenamientoKey.ERROR_ELIMINACION_DETALLE, cause);
    }

    private static MinioOperationException crear(String codigo, AlmacenamientoKey mensaje,
                                                 AlmacenamientoKey detalle, Throwable cause) {
        return new MinioOperationException(
                BaseError.conDetalle(codigo, Mensajes.obtener(mensaje), Mensajes.obtener(detalle)), cause);
    }
}
