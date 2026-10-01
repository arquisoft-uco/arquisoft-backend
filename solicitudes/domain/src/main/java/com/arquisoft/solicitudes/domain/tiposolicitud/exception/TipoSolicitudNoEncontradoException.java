package com.arquisoft.solicitudes.domain.tiposolicitud.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.key.solicitudes.TipoSolicitudKey;

public final class TipoSolicitudNoEncontradoException extends DomainException {

    public TipoSolicitudNoEncontradoException(String id) {
        super(
                Mensajes.formatear(TipoSolicitudKey.ERROR_TIPO_NO_ENCONTRADO, id),
                SolicitudesCodes.TipoSolicitud.TIPO_NO_ENCONTRADO
        );
    }
}
