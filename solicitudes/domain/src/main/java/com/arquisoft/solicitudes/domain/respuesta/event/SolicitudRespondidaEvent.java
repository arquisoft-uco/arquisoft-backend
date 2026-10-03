package com.arquisoft.solicitudes.domain.respuesta.event;

import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.solicitudes.domain.respuesta.RespuestaDomain;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import com.arquisoft.solicitudes.domain.tiposolicitud.exception.TipoSolicitudNoEncontradoException;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;

public abstract class SolicitudRespondidaEvent extends DomainEvent {

    protected SolicitudRespondidaEvent(String temaEvento, String tipoEvento) {
        super(temaEvento, tipoEvento);
    }

    public static SolicitudRespondidaEvent crear(TipoSolicitud tipoSolicitud, RespuestaDomain respuesta,
                                                 UsuarioDomain remitente, UsuarioDomain responsable) {
        return switch (tipoSolicitud) {
            case NOVEDAD_PARA_EL_COORDINADOR -> new SolicitudNovedadCoordinadorRespondidaEvent(
                    respuesta.getSolicitud(), respuesta.getId(), respuesta.getContenido(),
                    respuesta.getEstadoRespuesta().getId(), remitente.getNombre(), remitente.getEmail(),
                    responsable.getNombre());
            default -> throw new TipoSolicitudNoEncontradoException(tipoSolicitud.getId());
        };
    }
}
