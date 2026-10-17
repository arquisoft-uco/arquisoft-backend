package com.arquisoft.solicitudes.domain.solicitud.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.key.solicitudes.SolicitudKey;

public final class SolicitudTipoNoCoincideException extends DomainException {

    public SolicitudTipoNoCoincideException() {
        super(
                Mensajes.formatear(SolicitudKey.ERROR_SOLICITUD_TIPO_NO_COINCIDE),
                SolicitudesCodes.Solicitud.SOLICITUD_TIPO_NO_COINCIDE
        );
    }
}
