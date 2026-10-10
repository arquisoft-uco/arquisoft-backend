package com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.mapper;

import com.arquisoft.fichas.application.observacionevaluacion.query.criteria.ObservacionEvaluacionCoordinadorCriteria;
import com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.model.ConsultarObservacionesEvaluacionCoordinadorQuery;

public final class ConsultarObservacionesEvaluacionCoordinadorMapper {

    private ConsultarObservacionesEvaluacionCoordinadorMapper() {}

    public static ObservacionEvaluacionCoordinadorCriteria toCriteria(
            ConsultarObservacionesEvaluacionCoordinadorQuery query) {
        return new ObservacionEvaluacionCoordinadorCriteria(query.fichaPerfil());
    }
}
