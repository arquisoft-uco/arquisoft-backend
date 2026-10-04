package com.arquisoft.solicitudes.domain.respuesta.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.key.solicitudes.RespuestaKey;

import java.util.UUID;

public final class EstadoRespuestaNoResolutivoException extends DomainException {

    public EstadoRespuestaNoResolutivoException(UUID solicitud, String nuevoEstado) {
        super(
                Mensajes.formatear(RespuestaKey.ERROR_ESTADO_RESPUESTA_NO_RESOLUTIVO, nuevoEstado, solicitud),
                SolicitudesCodes.Respuesta.ESTADO_NO_RESOLUTIVO
        );
    }
}
