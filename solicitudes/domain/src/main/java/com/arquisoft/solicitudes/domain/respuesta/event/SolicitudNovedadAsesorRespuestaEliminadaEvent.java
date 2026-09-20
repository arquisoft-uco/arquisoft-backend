package com.arquisoft.solicitudes.domain.respuesta.event;

import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;

import java.util.UUID;

public final class SolicitudNovedadAsesorRespuestaEliminadaEvent extends DomainEvent {

    public static final String EVENT_TOPIC = EventTopics.Solicitudes.NOVEDAD_ASESOR_RESPUESTA_ELIMINADA;
    public static final String EVENT_TYPE = "SolicitudNovedadAsesorRespuestaEliminadaEvent";

    private final UUID solicitudId;
    private final UUID asesorUsuario;
    private final String tipoSolicitud;

    public SolicitudNovedadAsesorRespuestaEliminadaEvent(UUID solicitudId, UUID asesorUsuario,
                                                         String tipoSolicitud) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.solicitudId = solicitudId;
        this.asesorUsuario = asesorUsuario;
        this.tipoSolicitud = tipoSolicitud;
    }

    public UUID getSolicitudId() {
        return solicitudId;
    }

    public UUID getAsesorUsuario() {
        return asesorUsuario;
    }

    public String getTipoSolicitud() {
        return tipoSolicitud;
    }
}
