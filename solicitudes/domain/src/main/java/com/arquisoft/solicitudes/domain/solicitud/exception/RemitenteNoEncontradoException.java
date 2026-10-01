package com.arquisoft.solicitudes.domain.solicitud.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.key.solicitudes.SolicitudKey;

import java.util.UUID;

public final class RemitenteNoEncontradoException extends DomainException {

    public RemitenteNoEncontradoException(UUID usuario) {
        super(
                Mensajes.formatear(SolicitudKey.ERROR_REMITENTE_NO_ENCONTRADO, usuario),
                SolicitudesCodes.Solicitud.REMITENTE_NO_ENCONTRADO
        );
    }
}
