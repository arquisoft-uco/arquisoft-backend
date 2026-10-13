package com.arquisoft.solicitudes.domain.respuesta.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.key.solicitudes.RespuestaKey;

public final class RespuestaNoEncontradaException extends DomainException {

    public RespuestaNoEncontradaException() {
        super(
                Mensajes.formatear(RespuestaKey.ERROR_RESPUESTA_NO_ENCONTRADA),
                SolicitudesCodes.Respuesta.RESPUESTA_NO_ENCONTRADA
        );
    }
}
