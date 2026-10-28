package com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.event;

import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;

import java.util.UUID;

public class EvaluacionesCuantitativasAsesorRegistradasEvent extends DomainEvent {

    public static final String EVENT_TOPIC = EventTopics.Evaluaciones.EVALUACIONES_CUANTITATIVAS_ASESOR_REGISTRADAS;
    public static final String EVENT_TYPE = "EvaluacionesCuantitativasAsesorRegistradasEvent";

    private final UUID entregableId;

    public EvaluacionesCuantitativasAsesorRegistradasEvent(UUID entregableId) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.entregableId = entregableId;
    }

    public UUID getEntregableId() {
        return entregableId;
    }
}
