package com.arquisoft.solicitudes.domain.solicitud.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.key.solicitudes.SolicitudKey;

import java.util.UUID;

public final class DestinatarioNoAsignadoException extends DomainException {

    public DestinatarioNoAsignadoException(UUID destinatarioUsuario, UUID remitenteUsuario) {
        super(
                Mensajes.formatear(
                        SolicitudKey.ERROR_DESTINATARIO_NO_ASIGNADO, destinatarioUsuario, remitenteUsuario),
                SolicitudesCodes.Solicitud.DESTINATARIO_NO_ASIGNADO
        );
    }
}
