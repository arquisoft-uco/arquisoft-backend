package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.model.ConsultarEvaluacionesFichaPerfilCoordinadorQuery;

import java.util.UUID;

public final class ConsultarEvaluacionesFichaPerfilCoordinadorRequestMapper {

    private ConsultarEvaluacionesFichaPerfilCoordinadorRequestMapper() {}

    public static ConsultarEvaluacionesFichaPerfilCoordinadorQuery toQuery(UUID fichaPerfilId) {
        return ConsultarEvaluacionesFichaPerfilCoordinadorQuery.crear(fichaPerfilId);
    }
}
