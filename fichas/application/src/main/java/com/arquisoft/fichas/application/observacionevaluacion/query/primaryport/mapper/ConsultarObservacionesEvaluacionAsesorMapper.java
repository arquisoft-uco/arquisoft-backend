package com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.mapper;

import com.arquisoft.fichas.application.observacionevaluacion.query.criteria.ObservacionEvaluacionAsesorCriteria;
import com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.model.ConsultarObservacionesEvaluacionAsesorQuery;

public final class ConsultarObservacionesEvaluacionAsesorMapper {

    private ConsultarObservacionesEvaluacionAsesorMapper() {}

    public static ObservacionEvaluacionAsesorCriteria toCriteria(
            ConsultarObservacionesEvaluacionAsesorQuery query) {
        return new ObservacionEvaluacionAsesorCriteria(query.evaluacionFichaPerfil(), query.asesorFicha());
    }
}
