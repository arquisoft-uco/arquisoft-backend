package com.arquisoft.solicitudes.domain.solicitud.event;

import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;

import java.util.UUID;

public final class SolicitudNovedadCoordinadorEliminadaEvent extends DomainEvent {

    public static final String EVENT_TOPIC = EventTopics.Solicitudes.NOVEDAD_COORDINADOR_ELIMINADA;
    public static final String EVENT_TYPE = "SolicitudNovedadCoordinadorEliminadaEvent";

    private final UUID solicitudId;
    private final UUID remitenteUsuario;
    private final String tipoSolicitud;

    public SolicitudNovedadCoordinadorEliminadaEvent(UUID solicitudId, UUID remitenteUsuario,
                                                     String tipoSolicitud) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.solicitudId = solicitudId;
        this.remitenteUsuario = remitenteUsuario;
        this.tipoSolicitud = tipoSolicitud;
    }

    public UUID getSolicitudId() {
        return solicitudId;
    }

    public UUID getRemitenteUsuario() {
        return remitenteUsuario;
    }

    public String getTipoSolicitud() {
        return tipoSolicitud;
    }
}
