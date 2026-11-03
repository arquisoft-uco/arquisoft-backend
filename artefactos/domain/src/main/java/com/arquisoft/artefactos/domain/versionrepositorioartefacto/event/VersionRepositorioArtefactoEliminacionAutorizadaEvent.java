package com.arquisoft.artefactos.domain.versionrepositorioartefacto.event;

import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;

import java.util.UUID;

public final class VersionRepositorioArtefactoEliminacionAutorizadaEvent extends DomainEvent {

    public static final String EVENT_TOPIC =
            EventTopics.Artefactos.VERSION_REPOSITORIO_ARTEFACTO_ELIMINACION_AUTORIZADA;
    private static final String EVENT_TYPE = "VersionRepositorioArtefactoEliminacionAutorizadaEvent";

    private final UUID versionRepositorioArtefacto;
    private final String nombreDocumento;
    private final int versionDocumento;
    private final UUID repositorioArtefacto;

    public VersionRepositorioArtefactoEliminacionAutorizadaEvent(VersionRepositorioArtefactoDomain version) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.versionRepositorioArtefacto = version.getId();
        this.nombreDocumento = version.getNombreDocumento();
        this.versionDocumento = version.getVersionDocumento();
        this.repositorioArtefacto = version.getRepositorioArtefacto();
    }

    public UUID getVersionRepositorioArtefacto() {
        return versionRepositorioArtefacto;
    }

    public String getNombreDocumento() {
        return nombreDocumento;
    }

    public int getVersionDocumento() {
        return versionDocumento;
    }

    public UUID getRepositorioArtefacto() {
        return repositorioArtefacto;
    }
}
