package com.arquisoft.artefactos.domain.revisionasesor.event;

import com.arquisoft.artefactos.domain.revisionasesor.model.ContactoAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.model.ContactoEstudiante;
import com.arquisoft.artefactos.domain.revisionasesor.model.VersionCerradaEvento;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class RevisionAsesorCerradaEvent extends DomainEvent {

    public static final String EVENT_TOPIC = EventTopics.Artefactos.REVISION_ASESOR_CERRADA;
    private static final String EVENT_TYPE = "RevisionAsesorCerradaEvent";

    private final String documento;
    private final int numeroVersion;
    private final VersionCerradaEvento version;
    private final ContactoAsesor asesor;
    private final List<ContactoEstudiante> estudiantes;

    public RevisionAsesorCerradaEvent(
            String documento, int numeroVersion, VersionCerradaEvento version, ContactoAsesor asesor,
            List<ContactoEstudiante> estudiantes) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.documento = documento;
        this.numeroVersion = numeroVersion;
        this.version = version;
        this.asesor = asesor;
        this.estudiantes = List.copyOf(estudiantes);
    }

    public String getDocumento() {
        return documento;
    }

    public int getNumeroVersion() {
        return numeroVersion;
    }

    public UUID getProyectoGrado() {
        return version.proyectoGrado();
    }

    public UUID getArtefacto() {
        return version.artefacto();
    }

    public UUID getVersionArtefacto() {
        return version.versionArtefacto();
    }

    public String getUriArchivo() {
        return version.uriArchivo();
    }

    public Instant getFechaVersion() {
        return version.fechaVersion();
    }

    public UUID getVersionRepositorioArtefacto() {
        return version.versionRepositorioArtefacto();
    }

    public UUID getRepositorio() {
        return version.repositorio();
    }

    public String getNombreRepositorio() {
        return version.nombreRepositorio();
    }

    public ContactoAsesor getAsesor() {
        return asesor;
    }

    public List<ContactoEstudiante> getEstudiantes() {
        return estudiantes;
    }
}
