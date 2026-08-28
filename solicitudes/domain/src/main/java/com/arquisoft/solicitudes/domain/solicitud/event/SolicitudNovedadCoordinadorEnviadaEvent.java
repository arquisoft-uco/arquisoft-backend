package com.arquisoft.solicitudes.domain.solicitud.event;

import com.arquisoft.shared.events.DomainEvent;

import java.time.LocalDateTime;
import java.util.UUID;

public final class SolicitudNovedadCoordinadorEnviadaEvent extends DomainEvent {

    public static final String EVENT_TOPIC = "solicitudes.solicitud.novedad_coordinador_enviada";
    public static final String EVENT_TYPE = "SolicitudNovedadCoordinadorEnviadaEvent";

    private final UUID solicitudId;
    private final String remitenteUsuarioId;
    private final String destinatarioUsuarioId;
    private final String mensajeSolicitud;
    private final LocalDateTime fechaCreacion;
    private final String tipoSolicitud;

    public SolicitudNovedadCoordinadorEnviadaEvent(UUID solicitudId, String remitenteUsuarioId,
                                                   String destinatarioUsuarioId, String mensajeSolicitud,
                                                   LocalDateTime fechaCreacion, String tipoSolicitud) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.solicitudId = solicitudId;
        this.remitenteUsuarioId = remitenteUsuarioId;
        this.destinatarioUsuarioId = destinatarioUsuarioId;
        this.mensajeSolicitud = mensajeSolicitud;
        this.fechaCreacion = fechaCreacion;
        this.tipoSolicitud = tipoSolicitud;
    }

    public UUID getSolicitudId() {
        return solicitudId;
    }

    public String getRemitenteUsuarioId() {
        return remitenteUsuarioId;
    }

    public String getDestinatarioUsuarioId() {
        return destinatarioUsuarioId;
    }

    public String getMensajeSolicitud() {
        return mensajeSolicitud;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public String getTipoSolicitud() {
        return tipoSolicitud;
    }
}
