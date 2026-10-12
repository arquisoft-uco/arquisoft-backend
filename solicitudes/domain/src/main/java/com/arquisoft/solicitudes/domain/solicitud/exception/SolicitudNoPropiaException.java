package com.arquisoft.solicitudes.domain.solicitud.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.key.solicitudes.SolicitudKey;

public final class SolicitudNoPropiaException extends DomainException {

    public SolicitudNoPropiaException() {
        super(
                Mensajes.formatear(SolicitudKey.ERROR_SOLICITUD_NO_PROPIA),
                SolicitudesCodes.Solicitud.SOLICITUD_NO_PROPIA
        );
    }
}
