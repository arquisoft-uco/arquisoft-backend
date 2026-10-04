package com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.event;

import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;

import java.util.UUID;

public class EvaluacionesCualitativasJuradoRegistradasEvent extends DomainEvent {

    public static final String EVENT_TOPIC = EventTopics.Evaluaciones.EVALUACIONES_CUALITATIVAS_JURADO_REGISTRADAS;
    public static final String EVENT_TYPE = "EvaluacionesCualitativasJuradoRegistradasEvent";

    private final UUID entregableId;

    public EvaluacionesCualitativasJuradoRegistradasEvent(UUID entregableId) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.entregableId = entregableId;
    }

    public UUID getEntregableId() {
        return entregableId;
    }
}
