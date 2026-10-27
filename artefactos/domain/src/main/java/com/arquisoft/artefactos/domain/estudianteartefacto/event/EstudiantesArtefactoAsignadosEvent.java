package com.arquisoft.artefactos.domain.estudianteartefacto.event;

import com.arquisoft.artefactos.domain.estudianteartefacto.model.ContactoAsesor;
import com.arquisoft.artefactos.domain.estudianteartefacto.model.ContactoEstudiante;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;

import java.util.List;

public final class EstudiantesArtefactoAsignadosEvent extends DomainEvent {

    public static final String EVENT_TOPIC = EventTopics.Artefactos.ESTUDIANTES_ARTEFACTO_ASIGNADOS;
    private static final String EVENT_TYPE = "EstudiantesArtefactoAsignadosEvent";

    private final List<String> documentos;
    private final List<ContactoEstudiante> estudiantes;
    private final ContactoAsesor asesor;

    public EstudiantesArtefactoAsignadosEvent(
            List<String> documentos, List<ContactoEstudiante> estudiantes, ContactoAsesor asesor) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.documentos = List.copyOf(documentos);
        this.estudiantes = List.copyOf(estudiantes);
        this.asesor = asesor;
    }

    public List<String> getDocumentos() {
        return documentos;
    }

    public List<ContactoEstudiante> getEstudiantes() {
        return estudiantes;
    }

    public ContactoAsesor getAsesor() {
        return asesor;
    }
}
