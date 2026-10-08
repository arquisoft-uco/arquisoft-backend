package com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.mapper;

import com.arquisoft.fichas.application.observacionevaluacion.query.criteria.ObservacionEvaluacionRepresentanteCriteria;
import com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.model.ConsultarObservacionesEvaluacionRepresentanteQuery;

public final class ConsultarObservacionesEvaluacionRepresentanteMapper {

    private ConsultarObservacionesEvaluacionRepresentanteMapper() {}

    public static ObservacionEvaluacionRepresentanteCriteria toCriteria(
            ConsultarObservacionesEvaluacionRepresentanteQuery query) {
        return new ObservacionEvaluacionRepresentanteCriteria(query.evaluacionFichaPerfil(), query.representanteComite());
    }
}
