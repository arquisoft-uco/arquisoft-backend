package com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.mapper;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.criteria.EvaluacionFichaPerfilCoordinadorCriteria;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.model.ConsultarEvaluacionesFichaPerfilCoordinadorQuery;

public final class ConsultarEvaluacionesFichaPerfilCoordinadorMapper {

    private ConsultarEvaluacionesFichaPerfilCoordinadorMapper() {}

    public static EvaluacionFichaPerfilCoordinadorCriteria toCriteria(
            ConsultarEvaluacionesFichaPerfilCoordinadorQuery query) {
        return new EvaluacionFichaPerfilCoordinadorCriteria(query.fichaPerfil());
    }
}
