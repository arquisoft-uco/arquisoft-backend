package com.arquisoft.solicitudes.domain.solicitud.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.key.solicitudes.SolicitudKey;

import java.util.UUID;

public final class SolicitudConRespuestasException extends DomainException {

    public SolicitudConRespuestasException(UUID solicitud) {
        super(
                Mensajes.formatear(SolicitudKey.ERROR_SOLICITUD_CON_RESPUESTAS, solicitud),
                SolicitudesCodes.Solicitud.SOLICITUD_CON_RESPUESTAS
        );
    }
}
