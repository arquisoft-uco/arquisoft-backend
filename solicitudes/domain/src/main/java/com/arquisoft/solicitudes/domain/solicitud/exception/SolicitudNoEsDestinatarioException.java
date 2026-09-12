package com.arquisoft.solicitudes.domain.solicitud.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.key.solicitudes.RespuestaKey;

import java.util.UUID;

public final class SolicitudNoEsDestinatarioException extends DomainException {

    public SolicitudNoEsDestinatarioException(UUID usuario) {
        super(
                Mensajes.formatear(RespuestaKey.ERROR_SOLICITUD_NO_ES_DESTINATARIO, usuario),
                SolicitudesCodes.Solicitud.SOLICITUD_NO_ES_DESTINATARIO
        );
    }
}
