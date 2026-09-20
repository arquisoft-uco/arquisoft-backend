package com.arquisoft.solicitudes.domain.respuesta.event;

import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;

import java.util.UUID;

public final class SolicitudNovedadAsesorRespondidaEvent extends DomainEvent {

    public static final String EVENT_TOPIC = EventTopics.Solicitudes.NOVEDAD_ASESOR_RESPONDIDA;
    public static final String EVENT_TYPE = "SolicitudNovedadAsesorRespondidaEvent";

    private final UUID solicitudId;
    private final UUID respuestaId;
    private final String contenido;
    private final String estadoRespuesta;
    private final String remitenteNombre;
    private final String remitenteEmail;
    private final String asesorNombre;

    public SolicitudNovedadAsesorRespondidaEvent(UUID solicitudId, UUID respuestaId, String contenido,
                                                  String estadoRespuesta, String remitenteNombre,
                                                  String remitenteEmail, String asesorNombre) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.solicitudId = solicitudId;
        this.respuestaId = respuestaId;
        this.contenido = contenido;
        this.estadoRespuesta = estadoRespuesta;
        this.remitenteNombre = remitenteNombre;
        this.remitenteEmail = remitenteEmail;
        this.asesorNombre = asesorNombre;
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

    public String getAsesorNombre() {
        return asesorNombre;
    }
}
