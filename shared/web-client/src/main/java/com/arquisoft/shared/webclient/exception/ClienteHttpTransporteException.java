package com.arquisoft.shared.webclient.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.app.ClienteHttpKey;

public final class ClienteHttpTransporteException extends InfrastructureException {

    public static final String CODIGO = "CLIENTE_HTTP_TRANSPORTE";

    public ClienteHttpTransporteException(String detalle, Throwable cause) {
        super(BaseError.conDetalle(CODIGO, Mensajes.obtener(ClienteHttpKey.ERROR_TRANSPORTE), detalle), cause);
    }
}
