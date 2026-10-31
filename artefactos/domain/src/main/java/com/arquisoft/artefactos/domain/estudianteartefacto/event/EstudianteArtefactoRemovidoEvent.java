package com.arquisoft.artefactos.domain.estudianteartefacto.event;

import com.arquisoft.artefactos.domain.estudianteartefacto.model.ContactoAsesor;
import com.arquisoft.artefactos.domain.estudianteartefacto.model.ContactoEstudiante;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;

public final class EstudianteArtefactoRemovidoEvent extends DomainEvent {

    public static final String EVENT_TOPIC = EventTopics.Artefactos.ESTUDIANTE_ARTEFACTO_REMOVIDO;
    private static final String EVENT_TYPE = "EstudianteArtefactoRemovidoEvent";

    private final String documento;
    private final ContactoEstudiante estudiante;
    private final ContactoAsesor asesor;

    public EstudianteArtefactoRemovidoEvent(String documento, ContactoEstudiante estudiante, ContactoAsesor asesor) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.documento = documento;
        this.estudiante = estudiante;
        this.asesor = asesor;
    }

    public String getDocumento() {
        return documento;
    }

    public ContactoEstudiante getEstudiante() {
        return estudiante;
    }

    public ContactoAsesor getAsesor() {
        return asesor;
    }
}
