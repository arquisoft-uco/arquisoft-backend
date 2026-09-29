package com.arquisoft.solicitudes.domain.estadorespuesta.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.key.solicitudes.EstadoRespuestaKey;

public final class EstadoRespuestaNoEncontradoException extends DomainException {

    public EstadoRespuestaNoEncontradoException(String id) {
        super(
                Mensajes.formatear(EstadoRespuestaKey.ERROR_ESTADO_RESPUESTA_NO_ENCONTRADO, id),
                SolicitudesCodes.EstadoRespuesta.ESTADO_NO_ENCONTRADO
        );
    }
}
