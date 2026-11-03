package com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.event;

import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;

import java.util.UUID;

public class CriterioEvaluacionCualitativaJuradoCambiadoEvent extends DomainEvent {

    public static final String EVENT_TOPIC =
            EventTopics.Evaluaciones.CRITERIO_EVALUACION_CUALITATIVA_JURADO_CAMBIADO;
    public static final String EVENT_TYPE = "CriterioEvaluacionCualitativaJuradoCambiadoEvent";

    private final UUID evaluacionCualitativaJurado;
    private final UUID evaluacionJurado;
    private final UUID item;
    private final UUID criterioAnterior;
    private final UUID criterio;
    private final UUID jurado;

    public CriterioEvaluacionCualitativaJuradoCambiadoEvent(
            UUID evaluacionCualitativaJurado,
            UUID evaluacionJurado,
            UUID item,
            UUID criterioAnterior,
            UUID criterio,
            UUID jurado) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.evaluacionCualitativaJurado = evaluacionCualitativaJurado;
        this.evaluacionJurado = evaluacionJurado;
        this.item = item;
        this.criterioAnterior = criterioAnterior;
        this.criterio = criterio;
        this.jurado = jurado;
    }

    public UUID getEvaluacionCualitativaJurado() {
        return evaluacionCualitativaJurado;
    }

    public UUID getEvaluacionJurado() {
        return evaluacionJurado;
    }

    public UUID getItem() {
        return item;
    }

    public UUID getCriterioAnterior() {
        return criterioAnterior;
    }

    public UUID getCriterio() {
        return criterio;
    }

    public UUID getJurado() {
        return jurado;
    }
}
