package com.arquisoft.solicitudes.domain.solicitud.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.key.solicitudes.SolicitudKey;

public final class SolicitudDuplicadaException extends DomainException {

    public SolicitudDuplicadaException() {
        super(
                Mensajes.formatear(SolicitudKey.ERROR_SOLICITUD_DUPLICADA),
                SolicitudesCodes.Solicitud.SOLICITUD_DUPLICADA
        );
    }
}
