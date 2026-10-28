package com.arquisoft.solicitudes.domain.respuesta.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.key.solicitudes.RespuestaKey;
import com.arquisoft.solicitudes.domain.estadorespuesta.EstadoRespuesta;

public final class EstadoRespuestaNoResolutivoException extends DomainException {

    public EstadoRespuestaNoResolutivoException(EstadoRespuesta nuevoEstado) {
        super(BaseError.conDetalle(
                SolicitudesCodes.Respuesta.ESTADO_NO_RESOLUTIVO,
                Mensajes.formatear(RespuestaKey.ERROR_ESTADO_RESPUESTA_NO_RESOLUTIVO, nuevoEstado.getNombre()),
                Mensajes.formatear(RespuestaKey.ERROR_ESTADO_RESPUESTA_NO_RESOLUTIVO_DETALLE, nuevoEstado.getId())
        ));
    }
}
