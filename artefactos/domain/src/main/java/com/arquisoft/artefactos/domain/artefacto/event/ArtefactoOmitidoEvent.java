package com.arquisoft.artefactos.domain.artefacto.event;

import com.arquisoft.artefactos.domain.artefacto.model.ContactoAsesor;
import com.arquisoft.artefactos.domain.artefacto.model.ContactoEstudiante;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;

import java.util.List;

public final class ArtefactoOmitidoEvent extends DomainEvent {

    public static final String EVENT_TOPIC = EventTopics.Artefactos.ARTEFACTO_OMITIDO;
    private static final String EVENT_TYPE = "ArtefactoOmitidoEvent";

    private final String documento;
    private final List<ContactoEstudiante> estudiantes;
    private final ContactoAsesor asesor;

    public ArtefactoOmitidoEvent(
            String documento, List<ContactoEstudiante> estudiantes, ContactoAsesor asesor) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.documento = documento;
        this.estudiantes = List.copyOf(estudiantes);
        this.asesor = asesor;
    }

    public String getDocumento() {
        return documento;
    }

    public List<ContactoEstudiante> getEstudiantes() {
        return estudiantes;
    }

    public ContactoAsesor getAsesor() {
        return asesor;
    }
}
