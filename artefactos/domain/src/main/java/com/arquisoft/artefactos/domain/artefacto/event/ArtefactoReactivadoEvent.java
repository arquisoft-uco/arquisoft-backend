package com.arquisoft.artefactos.domain.artefacto.event;

import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;

import java.util.UUID;

public final class ArtefactoReactivadoEvent extends DomainEvent {

    public static final String EVENT_TOPIC = EventTopics.Artefactos.ARTEFACTO_REACTIVADO;
    private static final String EVENT_TYPE = "ArtefactoReactivadoEvent";

    private final UUID artefacto;
    private final UUID proyectoGrado;

    public ArtefactoReactivadoEvent(UUID artefacto, UUID proyectoGrado) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.artefacto = artefacto;
        this.proyectoGrado = proyectoGrado;
    }

    public UUID getArtefacto() {
        return artefacto;
    }

    public UUID getProyectoGrado() {
        return proyectoGrado;
    }
}
