package com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.mapper;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.criteria.EvaluacionFichaPerfilEstudianteCriteria;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.model.ConsultarEvaluacionesFichaPerfilEstudianteQuery;

public final class ConsultarEvaluacionesFichaPerfilEstudianteMapper {

    private ConsultarEvaluacionesFichaPerfilEstudianteMapper() {}

    public static EvaluacionFichaPerfilEstudianteCriteria toCriteria(
            ConsultarEvaluacionesFichaPerfilEstudianteQuery query) {
        return new EvaluacionFichaPerfilEstudianteCriteria(query.fichaPerfil(), query.estudiante());
    }
}
