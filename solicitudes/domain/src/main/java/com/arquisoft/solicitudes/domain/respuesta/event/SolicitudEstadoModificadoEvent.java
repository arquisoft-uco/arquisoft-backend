package com.arquisoft.solicitudes.domain.respuesta.event;

import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.solicitudes.domain.estadorespuesta.EstadoRespuesta;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import com.arquisoft.solicitudes.domain.tiposolicitud.exception.TipoSolicitudNoEncontradoException;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;

import java.util.Map;
import java.util.UUID;

public final class SolicitudEstadoModificadoEvent extends DomainEvent {

    private record IdentidadEvento(String tema, String tipoEvento) {}

    private static final Map<TipoSolicitud, IdentidadEvento> IDENTIDADES = Map.of(
            TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR, new IdentidadEvento(
                    EventTopics.Solicitudes.NOVEDAD_COORDINADOR_ESTADO_MODIFICADO,
                    "SolicitudNovedadCoordinadorEstadoModificadoEvent"));

    private final UUID solicitudId;
    private final String nuevoEstado;
    private final String nuevoEstadoNombre;
    private final String remitenteNombre;
    private final String remitenteEmail;
    private final String responsableNombre;

    public SolicitudEstadoModificadoEvent(TipoSolicitud tipoSolicitud, UUID solicitudId,
                                          EstadoRespuesta nuevoEstado, UsuarioDomain remitente,
                                          UsuarioDomain responsable) {
        super(identidadDe(tipoSolicitud).tema(), identidadDe(tipoSolicitud).tipoEvento());
        this.solicitudId = solicitudId;
        this.nuevoEstado = nuevoEstado.getId();
        this.nuevoEstadoNombre = nuevoEstado.getNombre();
        this.remitenteNombre = remitente.getNombre();
        this.remitenteEmail = remitente.getEmail();
        this.responsableNombre = responsable.getNombre();
    }

    private static IdentidadEvento identidadDe(TipoSolicitud tipoSolicitud) {
        var identidad = IDENTIDADES.get(tipoSolicitud);
        if (UtilObjeto.esNulo(identidad)) {
            throw new TipoSolicitudNoEncontradoException(tipoSolicitud.getId());
        }
        return identidad;
    }

    public UUID getSolicitudId() {
        return solicitudId;
    }

    public String getNuevoEstado() {
        return nuevoEstado;
    }

    public String getNuevoEstadoNombre() {
        return nuevoEstadoNombre;
    }

    public String getRemitenteNombre() {
        return remitenteNombre;
    }

    public String getRemitenteEmail() {
        return remitenteEmail;
    }

    public String getResponsableNombre() {
        return responsableNombre;
    }
}
