package com.arquisoft.entregables.domain.entregableproyectogrado.event;

import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;

import java.util.UUID;

public final class EntregableProyectoGradoCanceladoEvent extends DomainEvent {

    public static final String EVENT_TOPIC = EventTopics.Entregables.ENTREGABLE_PROYECTO_GRADO_CANCELADO;
    private static final String EVENT_TYPE = "EntregableProyectoGradoCanceladoEvent";

    private final UUID entregableProyectoGrado;
    private final UUID proyectoGrado;
    private final int version;

    public EntregableProyectoGradoCanceladoEvent(UUID entregableProyectoGrado, UUID proyectoGrado, int version) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.entregableProyectoGrado = entregableProyectoGrado;
        this.proyectoGrado = proyectoGrado;
        this.version = version;
    }

    public UUID getEntregableProyectoGrado() {
        return entregableProyectoGrado;
    }

    public UUID getProyectoGrado() {
        return proyectoGrado;
    }

    public int getVersion() {
        return version;
    }
}
