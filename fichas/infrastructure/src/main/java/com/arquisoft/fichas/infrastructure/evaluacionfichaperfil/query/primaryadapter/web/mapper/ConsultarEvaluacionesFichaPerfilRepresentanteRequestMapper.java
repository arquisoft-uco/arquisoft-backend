package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.model.ConsultarEvaluacionesFichaPerfilRepresentanteQuery;

import java.util.UUID;

public final class ConsultarEvaluacionesFichaPerfilRepresentanteRequestMapper {

    private ConsultarEvaluacionesFichaPerfilRepresentanteRequestMapper() {}

    public static ConsultarEvaluacionesFichaPerfilRepresentanteQuery toQuery(UUID fichaPerfilId, UUID representanteId) {
        return ConsultarEvaluacionesFichaPerfilRepresentanteQuery.crear(fichaPerfilId, representanteId);
    }
}
