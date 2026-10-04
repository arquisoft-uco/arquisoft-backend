package com.arquisoft.solicitudes.domain.respuesta.event;

import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.solicitudes.domain.estadorespuesta.EstadoRespuesta;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import com.arquisoft.solicitudes.domain.tiposolicitud.exception.TipoSolicitudNoEncontradoException;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;

import java.util.UUID;

public abstract class SolicitudEstadoModificadoEvent extends DomainEvent {

    protected SolicitudEstadoModificadoEvent(String temaEvento, String tipoEvento) {
        super(temaEvento, tipoEvento);
    }

    public static SolicitudEstadoModificadoEvent crear(TipoSolicitud tipoSolicitud, UUID solicitud,
                                                       EstadoRespuesta nuevoEstado, UsuarioDomain remitente,
                                                       UsuarioDomain responsable) {
        return switch (tipoSolicitud) {
            case NOVEDAD_PARA_EL_COORDINADOR -> new SolicitudNovedadCoordinadorEstadoModificadoEvent(
                    solicitud, nuevoEstado.getId(), nuevoEstado.getNombre(),
                    remitente.getNombre(), remitente.getEmail(), responsable.getNombre());
            default -> throw new TipoSolicitudNoEncontradoException(tipoSolicitud.getId());
        };
    }
}
