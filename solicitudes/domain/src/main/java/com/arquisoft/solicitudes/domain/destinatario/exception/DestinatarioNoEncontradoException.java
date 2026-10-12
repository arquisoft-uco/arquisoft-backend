package com.arquisoft.solicitudes.domain.destinatario.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.key.solicitudes.SolicitudKey;

public final class DestinatarioNoEncontradoException extends DomainException {

    public DestinatarioNoEncontradoException() {
        super(
                Mensajes.formatear(SolicitudKey.ERROR_DESTINATARIO_NO_ENCONTRADO),
                SolicitudesCodes.Solicitud.DESTINATARIO_NO_ENCONTRADO
        );
    }
}
