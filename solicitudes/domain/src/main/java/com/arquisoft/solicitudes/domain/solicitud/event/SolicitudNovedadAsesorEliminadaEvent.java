package com.arquisoft.solicitudes.domain.solicitud.event;

import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;

import java.util.UUID;

public final class SolicitudNovedadAsesorEliminadaEvent extends DomainEvent {

    public static final String EVENT_TOPIC = EventTopics.Solicitudes.NOVEDAD_ASESOR_ELIMINADA;
    public static final String EVENT_TYPE = "SolicitudNovedadAsesorEliminadaEvent";

    private final UUID solicitudId;
    private final UUID remitenteUsuario;
    private final String tipoSolicitud;

    public SolicitudNovedadAsesorEliminadaEvent(UUID solicitudId, UUID remitenteUsuario,
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
