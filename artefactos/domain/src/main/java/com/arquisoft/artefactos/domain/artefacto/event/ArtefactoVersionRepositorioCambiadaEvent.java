package com.arquisoft.artefactos.domain.artefacto.event;

import com.arquisoft.artefactos.domain.artefacto.model.ContactoAsesor;
import com.arquisoft.artefactos.domain.artefacto.model.ContactoEstudiante;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;

import java.util.List;

public final class ArtefactoVersionRepositorioCambiadaEvent extends DomainEvent {

    public static final String EVENT_TOPIC = EventTopics.Artefactos.ARTEFACTO_VERSION_REPOSITORIO_CAMBIADA;
    private static final String EVENT_TYPE = "ArtefactoVersionRepositorioCambiadaEvent";

    private final String documento;
    private final int versionAnterior;
    private final int versionNueva;
    private final List<ContactoEstudiante> estudiantes;
    private final ContactoAsesor asesor;

    public ArtefactoVersionRepositorioCambiadaEvent(
            String documento, int versionAnterior, int versionNueva,
            List<ContactoEstudiante> estudiantes, ContactoAsesor asesor) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.documento = documento;
        this.versionAnterior = versionAnterior;
        this.versionNueva = versionNueva;
        this.estudiantes = List.copyOf(estudiantes);
        this.asesor = asesor;
    }

    public String getDocumento() {
        return documento;
    }

    public int getVersionAnterior() {
        return versionAnterior;
    }

    public int getVersionNueva() {
        return versionNueva;
    }

    public List<ContactoEstudiante> getEstudiantes() {
        return estudiantes;
    }

    public ContactoAsesor getAsesor() {
        return asesor;
    }
}
