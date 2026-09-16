package com.arquisoft.solicitudes.domain.respuesta.event;

import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;

import java.util.UUID;

public final class SolicitudNovedadCoordinadorEstadoModificadoEvent extends DomainEvent {

    public static final String EVENT_TOPIC = EventTopics.Solicitudes.NOVEDAD_COORDINADOR_ESTADO_MODIFICADO;
    public static final String EVENT_TYPE = "SolicitudNovedadCoordinadorEstadoModificadoEvent";

    private final UUID solicitudId;
    private final String nuevoEstado;
    private final String nuevoEstadoNombre;
    private final String remitenteNombre;
    private final String remitenteEmail;
    private final String coordinadorNombre;

    public SolicitudNovedadCoordinadorEstadoModificadoEvent(UUID solicitudId, String nuevoEstado,
                                                             String nuevoEstadoNombre, String remitenteNombre,
                                                             String remitenteEmail, String coordinadorNombre) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.solicitudId = solicitudId;
        this.nuevoEstado = nuevoEstado;
        this.nuevoEstadoNombre = nuevoEstadoNombre;
        this.remitenteNombre = remitenteNombre;
        this.remitenteEmail = remitenteEmail;
        this.coordinadorNombre = coordinadorNombre;
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

    public String getCoordinadorNombre() {
        return coordinadorNombre;
    }
}
