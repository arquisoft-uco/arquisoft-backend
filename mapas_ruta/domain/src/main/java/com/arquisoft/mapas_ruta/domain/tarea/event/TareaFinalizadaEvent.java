package com.arquisoft.mapas_ruta.domain.tarea.event;

import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;

import java.util.UUID;

public class TareaFinalizadaEvent extends DomainEvent {

    public static final String EVENT_TOPIC = EventTopics.MapasRuta.TAREA_FINALIZADA;
    public static final String EVENT_TYPE = "TareaFinalizadaEvent";

    private final UUID tarea;

    public TareaFinalizadaEvent(UUID tarea) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.tarea = tarea;
    }

    public UUID getTarea() {
        return tarea;
    }
}
