package com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.mapper;

import com.arquisoft.fichas.application.observacionevaluacion.query.criteria.ObservacionEvaluacionEstudianteCriteria;
import com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.model.ConsultarObservacionesEvaluacionEstudianteQuery;

public final class ConsultarObservacionesEvaluacionEstudianteMapper {

    private ConsultarObservacionesEvaluacionEstudianteMapper() {}

    public static ObservacionEvaluacionEstudianteCriteria toCriteria(
            ConsultarObservacionesEvaluacionEstudianteQuery query) {
        return new ObservacionEvaluacionEstudianteCriteria(query.evaluacionFichaPerfil(), query.estudiante());
    }
}
