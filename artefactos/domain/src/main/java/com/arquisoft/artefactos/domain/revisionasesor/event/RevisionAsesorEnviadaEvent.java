package com.arquisoft.artefactos.domain.revisionasesor.event;

import com.arquisoft.artefactos.domain.revisionasesor.model.ContactoAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.model.ContactoEstudiante;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;

import java.util.List;

public final class RevisionAsesorEnviadaEvent extends DomainEvent {

    public static final String EVENT_TOPIC = EventTopics.Artefactos.REVISION_ASESOR_ENVIADA;
    private static final String EVENT_TYPE = "RevisionAsesorEnviadaEvent";

    private final String documento;
    private final int numeroVersion;
    private final ContactoAsesor asesor;
    private final List<ContactoEstudiante> estudiantes;

    public RevisionAsesorEnviadaEvent(
            String documento, int numeroVersion, ContactoAsesor asesor, List<ContactoEstudiante> estudiantes) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.documento = documento;
        this.numeroVersion = numeroVersion;
        this.asesor = asesor;
        this.estudiantes = List.copyOf(estudiantes);
    }

    public String getDocumento() {
        return documento;
    }

    public int getNumeroVersion() {
        return numeroVersion;
    }

    public ContactoAsesor getAsesor() {
        return asesor;
    }

    public List<ContactoEstudiante> getEstudiantes() {
        return estudiantes;
    }
}
