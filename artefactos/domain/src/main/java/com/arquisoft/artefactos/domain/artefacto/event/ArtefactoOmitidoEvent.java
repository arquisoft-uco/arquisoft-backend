package com.arquisoft.artefactos.domain.artefacto.event;

import com.arquisoft.artefactos.domain.artefacto.model.ContactoAsesor;
import com.arquisoft.artefactos.domain.artefacto.model.ContactoEstudiante;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;

import java.util.List;
import java.util.UUID;

public final class ArtefactoOmitidoEvent extends DomainEvent {

    public static final String EVENT_TOPIC = EventTopics.Artefactos.ARTEFACTO_OMITIDO;
    private static final String EVENT_TYPE = "ArtefactoOmitidoEvent";

    private final String documento;
    private final List<ContactoEstudiante> estudiantes;
    private final ContactoAsesor asesor;
    private final UUID artefacto;
    private final UUID proyectoGrado;

    public ArtefactoOmitidoEvent(
            String documento, List<ContactoEstudiante> estudiantes, ContactoAsesor asesor, UUID artefacto,
            UUID proyectoGrado) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.documento = documento;
        this.estudiantes = List.copyOf(estudiantes);
        this.asesor = asesor;
        this.artefacto = artefacto;
        this.proyectoGrado = proyectoGrado;
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

    public UUID getArtefacto() {
        return artefacto;
    }

    public UUID getProyectoGrado() {
        return proyectoGrado;
    }
}
