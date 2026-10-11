package com.arquisoft.solicitudes.domain.remitente.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.key.solicitudes.SolicitudKey;

public final class RemitenteNoEncontradoException extends DomainException {

    public RemitenteNoEncontradoException() {
        super(
                Mensajes.formatear(SolicitudKey.ERROR_REMITENTE_NO_ENCONTRADO),
                SolicitudesCodes.Solicitud.REMITENTE_NO_ENCONTRADO
        );
    }
}
