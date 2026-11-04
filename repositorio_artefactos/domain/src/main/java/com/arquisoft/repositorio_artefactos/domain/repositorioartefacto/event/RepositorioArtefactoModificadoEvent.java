package com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.event;

import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.ModificacionRepositorioArtefactoDomain;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;

import java.util.UUID;

public final class RepositorioArtefactoModificadoEvent extends DomainEvent {

    public static final String EVENT_TOPIC = EventTopics.RepositorioArtefactos.REPOSITORIO_ARTEFACTO_MODIFICADO;
    private static final String EVENT_TYPE = "RepositorioArtefactoModificadoEvent";

    private final UUID repositorioArtefacto;
    private final String nombreRepositorio;

    public RepositorioArtefactoModificadoEvent(ModificacionRepositorioArtefactoDomain modificacion) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.repositorioArtefacto = modificacion.getRepositorio();
        this.nombreRepositorio = modificacion.getNombreRepositorio();
    }

    public UUID getRepositorioArtefacto() {
        return repositorioArtefacto;
    }

    public String getNombreRepositorio() {
        return nombreRepositorio;
    }
}
