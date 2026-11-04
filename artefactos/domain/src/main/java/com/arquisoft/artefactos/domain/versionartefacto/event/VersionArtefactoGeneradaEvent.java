package com.arquisoft.artefactos.domain.versionartefacto.event;

import com.arquisoft.artefactos.domain.versionartefacto.VersionArtefactoDomain;
import com.arquisoft.artefactos.domain.versionartefacto.model.ContactoAsesor;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;

import java.util.UUID;

public final class VersionArtefactoGeneradaEvent extends DomainEvent {

    public static final String EVENT_TOPIC = EventTopics.Artefactos.VERSION_ARTEFACTO_GENERADA;
    private static final String EVENT_TYPE = "VersionArtefactoGeneradaEvent";

    private final UUID versionArtefacto;
    private final UUID artefacto;
    private final int numeroVersion;
    private final String nombreDocumento;
    private final ContactoAsesor asesor;

    public VersionArtefactoGeneradaEvent(
            VersionArtefactoDomain version, String nombreDocumento, ContactoAsesor asesor) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.versionArtefacto = version.getId();
        this.artefacto = version.getArtefacto();
        this.numeroVersion = version.getNumeroVersion();
        this.nombreDocumento = nombreDocumento;
        this.asesor = asesor;
    }

    public UUID getVersionArtefacto() {
        return versionArtefacto;
    }

    public UUID getArtefacto() {
        return artefacto;
    }

    public int getNumeroVersion() {
        return numeroVersion;
    }

    public String getNombreDocumento() {
        return nombreDocumento;
    }

    public ContactoAsesor getAsesor() {
        return asesor;
    }
}
