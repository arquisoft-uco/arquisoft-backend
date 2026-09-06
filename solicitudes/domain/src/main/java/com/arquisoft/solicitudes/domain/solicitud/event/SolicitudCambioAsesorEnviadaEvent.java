package com.arquisoft.solicitudes.domain.solicitud.event;

import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;

import java.util.UUID;

public final class SolicitudCambioAsesorEnviadaEvent extends DomainEvent {

    public static final String EVENT_TOPIC = EventTopics.Solicitudes.CAMBIO_ASESOR_ENVIADA;
    public static final String EVENT_TYPE = "SolicitudCambioAsesorEnviadaEvent";

    private final UUID solicitudId;
    private final String remitenteNombre;
    private final String destinatarioNombre;
    private final String destinatarioEmail;
    private final String mensajeSolicitud;

    public SolicitudCambioAsesorEnviadaEvent(UUID solicitudId, String remitenteNombre,
                                             String destinatarioNombre, String destinatarioEmail,
                                             String mensajeSolicitud) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.solicitudId = solicitudId;
        this.remitenteNombre = remitenteNombre;
        this.destinatarioNombre = destinatarioNombre;
        this.destinatarioEmail = destinatarioEmail;
        this.mensajeSolicitud = mensajeSolicitud;
    }

    public UUID getSolicitudId() {
        return solicitudId;
    }

    public String getRemitenteNombre() {
        return remitenteNombre;
    }

    public String getDestinatarioNombre() {
        return destinatarioNombre;
    }

    public String getDestinatarioEmail() {
        return destinatarioEmail;
    }

    public String getMensajeSolicitud() {
        return mensajeSolicitud;
    }
}
