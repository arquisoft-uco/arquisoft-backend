package com.arquisoft.solicitudes.domain.respuesta.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.key.solicitudes.RespuestaKey;

import java.util.UUID;

public final class RespuestaNoEncontradaException extends DomainException {

    public RespuestaNoEncontradaException(UUID solicitud) {
        super(
                Mensajes.formatear(RespuestaKey.ERROR_RESPUESTA_NO_ENCONTRADA, solicitud),
                SolicitudesCodes.Respuesta.RESPUESTA_NO_ENCONTRADA
        );
    }
}
