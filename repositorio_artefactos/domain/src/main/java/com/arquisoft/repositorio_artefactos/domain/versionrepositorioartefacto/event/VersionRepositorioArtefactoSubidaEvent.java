package com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.event;

import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;

import java.util.UUID;

public final class VersionRepositorioArtefactoSubidaEvent extends DomainEvent {

    public static final String EVENT_TOPIC = EventTopics.RepositorioArtefactos.VERSION_REPOSITORIO_ARTEFACTO_SUBIDA;
    private static final String EVENT_TYPE = "VersionRepositorioArtefactoSubidaEvent";

    private final UUID versionRepositorioArtefacto;
    private final String nombreDocumento;
    private final int versionDocumento;
    private final UUID repositorioArtefacto;
    private final String nombreRepositorio;
    private final String nombreArchivo;
    private final String tipoContenido;
    private final long tamanoBytes;

    public VersionRepositorioArtefactoSubidaEvent(VersionRepositorioArtefactoDomain version, String nombreRepositorio) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.versionRepositorioArtefacto = version.getId();
        this.nombreDocumento = version.getNombreDocumento();
        this.versionDocumento = version.getNumeroVersion();
        this.repositorioArtefacto = version.getRepositorioArtefacto();
        this.nombreRepositorio = nombreRepositorio;
        this.nombreArchivo = version.getNombreArchivo();
        this.tipoContenido = version.getTipoContenido();
        this.tamanoBytes = version.getTamanoBytes();
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

    public String getNombreRepositorio() {
        return nombreRepositorio;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public String getTipoContenido() {
        return tipoContenido;
    }

    public long getTamanoBytes() {
        return tamanoBytes;
    }
}
