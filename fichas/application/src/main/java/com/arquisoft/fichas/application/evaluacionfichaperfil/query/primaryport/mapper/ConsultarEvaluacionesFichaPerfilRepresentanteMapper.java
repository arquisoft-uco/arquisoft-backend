package com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.mapper;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.criteria.EvaluacionFichaPerfilRepresentanteCriteria;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.model.ConsultarEvaluacionesFichaPerfilRepresentanteQuery;

public final class ConsultarEvaluacionesFichaPerfilRepresentanteMapper {

    private ConsultarEvaluacionesFichaPerfilRepresentanteMapper() {}

    public static EvaluacionFichaPerfilRepresentanteCriteria toCriteria(
            ConsultarEvaluacionesFichaPerfilRepresentanteQuery query) {
        return new EvaluacionFichaPerfilRepresentanteCriteria(query.fichaPerfil(), query.representanteComite());
    }
}
