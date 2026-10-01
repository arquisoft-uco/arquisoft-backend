package com.arquisoft.solicitudes.domain.solicitud.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.key.solicitudes.SolicitudKey;

import java.util.UUID;

public final class DestinatarioNoEncontradoException extends DomainException {

    public DestinatarioNoEncontradoException(UUID usuario) {
        super(
                Mensajes.formatear(SolicitudKey.ERROR_DESTINATARIO_NO_ENCONTRADO, usuario),
                SolicitudesCodes.Solicitud.DESTINATARIO_NO_ENCONTRADO
        );
    }
}
