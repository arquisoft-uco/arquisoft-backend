package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.model.ConsultarEvaluacionesFichaPerfilEstudianteQuery;

import java.util.UUID;

public final class ConsultarEvaluacionesFichaPerfilEstudianteRequestMapper {

    private ConsultarEvaluacionesFichaPerfilEstudianteRequestMapper() {}

    public static ConsultarEvaluacionesFichaPerfilEstudianteQuery toQuery(UUID fichaPerfilId, UUID estudiante) {
        return ConsultarEvaluacionesFichaPerfilEstudianteQuery.crear(fichaPerfilId, estudiante);
    }
}
