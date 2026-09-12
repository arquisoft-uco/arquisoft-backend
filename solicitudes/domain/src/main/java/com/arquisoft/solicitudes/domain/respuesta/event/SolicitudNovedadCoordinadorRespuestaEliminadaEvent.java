package com.arquisoft.solicitudes.domain.respuesta.event;

import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;

import java.util.UUID;

public final class SolicitudNovedadCoordinadorRespuestaEliminadaEvent extends DomainEvent {

    public static final String EVENT_TOPIC = EventTopics.Solicitudes.NOVEDAD_COORDINADOR_RESPUESTA_ELIMINADA;
    public static final String EVENT_TYPE = "SolicitudNovedadCoordinadorRespuestaEliminadaEvent";

    private final UUID solicitudId;
    private final UUID coordinadorUsuario;
    private final String tipoSolicitud;

    public SolicitudNovedadCoordinadorRespuestaEliminadaEvent(UUID solicitudId, UUID coordinadorUsuario,
                                                               String tipoSolicitud) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.solicitudId = solicitudId;
        this.coordinadorUsuario = coordinadorUsuario;
        this.tipoSolicitud = tipoSolicitud;
    }

    public UUID getSolicitudId() {
        return solicitudId;
    }

    public UUID getCoordinadorUsuario() {
        return coordinadorUsuario;
    }

    public String getTipoSolicitud() {
        return tipoSolicitud;
    }
}
