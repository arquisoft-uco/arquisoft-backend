package com.arquisoft.artefactos.domain.revisionasesor.event;

import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;

import java.util.UUID;

public class RevisionAsesorEstadoCambiadoEvent extends DomainEvent {

    public static final String EVENT_TOPIC = EventTopics.Artefactos.REVISION_ASESOR_ESTADO_CAMBIADO;
    public static final String EVENT_TYPE = "RevisionAsesorEstadoCambiadoEvent";

    private final UUID revisionAsesorId;
    private final String estado;
    private final String asesorNombre;
    private final String asesorEmail;
    private final String estudianteNombre;

    public RevisionAsesorEstadoCambiadoEvent(
            UUID revisionAsesorId,
            String estado,
            String asesorNombre,
            String asesorEmail,
            String estudianteNombre) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.revisionAsesorId = revisionAsesorId;
        this.estado = estado;
        this.asesorNombre = asesorNombre;
        this.asesorEmail = asesorEmail;
        this.estudianteNombre = estudianteNombre;
    }

    public UUID getRevisionAsesorId() {
        return revisionAsesorId;
    }

    public String getEstado() {
        return estado;
    }

    public String getAsesorNombre() {
        return asesorNombre;
    }

    public String getAsesorEmail() {
        return asesorEmail;
    }

    public String getEstudianteNombre() {
        return estudianteNombre;
    }
}
