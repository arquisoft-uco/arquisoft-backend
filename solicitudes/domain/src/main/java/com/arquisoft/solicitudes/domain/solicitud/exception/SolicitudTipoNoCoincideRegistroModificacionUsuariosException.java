package com.arquisoft.solicitudes.domain.solicitud.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.key.solicitudes.SolicitudKey;

public final class SolicitudTipoNoCoincideRegistroModificacionUsuariosException extends DomainException {

    public SolicitudTipoNoCoincideRegistroModificacionUsuariosException() {
        super(
                Mensajes.formatear(SolicitudKey.ERROR_SOLICITUD_TIPO_NO_COINCIDE_REGISTRO_MODIFICACION_USUARIOS),
                SolicitudesCodes.Solicitud.SOLICITUD_TIPO_NO_COINCIDE_REGISTRO_MODIFICACION_USUARIOS
        );
    }
}
