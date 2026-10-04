package com.arquisoft.solicitudes.domain.solicitud.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.key.solicitudes.SolicitudKey;

import java.util.UUID;

public final class SolicitudNoEsDestinatarioException extends DomainException {

    public SolicitudNoEsDestinatarioException(UUID usuario) {
        super(
                Mensajes.formatear(SolicitudKey.ERROR_SOLICITUD_NO_ES_DESTINATARIO, usuario),
                SolicitudesCodes.Solicitud.SOLICITUD_NO_ES_DESTINATARIO
        );
    }
}
