package com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.event;

import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;

import java.util.UUID;

public class EvaluacionesCualitativasAsesorRegistradasEvent extends DomainEvent {

    public static final String EVENT_TOPIC = EventTopics.Evaluaciones.EVALUACIONES_CUALITATIVAS_ASESOR_REGISTRADAS;
    public static final String EVENT_TYPE = "EvaluacionesCualitativasAsesorRegistradasEvent";

    private final UUID entregableId;

    public EvaluacionesCualitativasAsesorRegistradasEvent(UUID entregableId) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.entregableId = entregableId;
    }

    public UUID getEntregableId() {
        return entregableId;
    }
}
