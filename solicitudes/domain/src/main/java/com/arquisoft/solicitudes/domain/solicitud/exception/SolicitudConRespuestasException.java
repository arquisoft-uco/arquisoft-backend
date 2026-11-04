package com.arquisoft.solicitudes.domain.solicitud.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.key.solicitudes.SolicitudKey;

public final class SolicitudConRespuestasException extends DomainException {

    public SolicitudConRespuestasException() {
        super(
                Mensajes.formatear(SolicitudKey.ERROR_SOLICITUD_CON_RESPUESTAS),
                SolicitudesCodes.Solicitud.SOLICITUD_CON_RESPUESTAS
        );
    }
}
