package com.arquisoft.solicitudes.domain.respuesta.event;

import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.solicitudes.domain.respuesta.RespuestaDomain;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import com.arquisoft.solicitudes.domain.tiposolicitud.exception.TipoSolicitudNoEncontradoException;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;

import java.util.Map;
import java.util.UUID;

public final class SolicitudRespondidaEvent extends DomainEvent {

    private record IdentidadEvento(String tema, String tipoEvento) {}

    private static final Map<TipoSolicitud, IdentidadEvento> IDENTIDADES = Map.of(
            TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR, new IdentidadEvento(
                    EventTopics.Solicitudes.NOVEDAD_COORDINADOR_RESPONDIDA,
                    "SolicitudNovedadCoordinadorRespondidaEvent"));

    private final UUID solicitudId;
    private final UUID respuestaId;
    private final String contenido;
    private final String estadoRespuesta;
    private final String remitenteNombre;
    private final String remitenteEmail;
    private final String responsableNombre;

    public SolicitudRespondidaEvent(TipoSolicitud tipoSolicitud, RespuestaDomain respuesta,
                                    UsuarioDomain remitente, UsuarioDomain responsable) {
        super(identidadDe(tipoSolicitud).tema(), identidadDe(tipoSolicitud).tipoEvento());
        this.solicitudId = respuesta.getSolicitud();
        this.respuestaId = respuesta.getId();
        this.contenido = respuesta.getContenido();
        this.estadoRespuesta = respuesta.getEstadoRespuesta().getId();
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

    public UUID getRespuestaId() {
        return respuestaId;
    }

    public String getContenido() {
        return contenido;
    }

    public String getEstadoRespuesta() {
        return estadoRespuesta;
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
