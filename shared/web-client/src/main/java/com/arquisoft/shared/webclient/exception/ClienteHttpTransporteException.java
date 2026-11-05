package com.arquisoft.shared.webclient.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.InfrastructureException;

public final class ClienteHttpTransporteException extends InfrastructureException {

    public static final String CODIGO = "CLIENTE_HTTP_TRANSPORTE";

    public ClienteHttpTransporteException(String message, Throwable cause) {
        super(BaseError.of(CODIGO, message, cause), cause);
    }
}
