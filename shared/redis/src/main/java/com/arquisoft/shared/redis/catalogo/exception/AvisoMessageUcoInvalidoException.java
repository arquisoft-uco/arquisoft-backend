package com.arquisoft.shared.redis.catalogo.exception;

import com.arquisoft.shared.exception.InfrastructureException;

/**
 * Un aviso de cambio de MessageUcoLab que no se puede leer. Reintentarlo no sirve: se descarta.
 */
public final class AvisoMessageUcoInvalidoException extends InfrastructureException {

    private static final String CODIGO = "CATALOGO_AVISO_MESSAGEUCO_INVALIDO";

    public AvisoMessageUcoInvalidoException(String mensaje, Throwable causa) {
        super(mensaje, CODIGO, causa);
    }
}
