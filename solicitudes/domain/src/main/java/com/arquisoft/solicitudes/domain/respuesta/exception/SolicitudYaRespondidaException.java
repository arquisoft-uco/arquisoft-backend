package com.arquisoft.solicitudes.domain.respuesta.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.key.solicitudes.RespuestaKey;

public final class SolicitudYaRespondidaException extends DomainException {

    public SolicitudYaRespondidaException() {
        super(
                Mensajes.formatear(RespuestaKey.ERROR_SOLICITUD_YA_RESPONDIDA),
                SolicitudesCodes.Respuesta.SOLICITUD_YA_RESPONDIDA
        );
    }
}
