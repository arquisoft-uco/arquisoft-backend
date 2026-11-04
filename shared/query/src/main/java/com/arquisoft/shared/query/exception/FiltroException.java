package com.arquisoft.shared.query.exception;

import com.arquisoft.shared.exception.ApplicationException;
import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.message.Mensajes;

public final class FiltroException extends ApplicationException {

    public FiltroException(String mensaje, String errorCode) {
        super(mensaje, errorCode);
    }

    public FiltroException(String errorCode, ClaveMensaje mensaje, ClaveMensaje detalle, Object... argumentos) {
        super(BaseError.conDetalle(errorCode, Mensajes.obtener(mensaje), Mensajes.formatear(detalle, argumentos)));
    }
}
