package com.arquisoft.fichas.infrastructure.estadofichaperfil.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.model.ConsultarEstadosFichaPerfilEstudianteQuery;

import java.util.UUID;

public final class ConsultarEstadosFichaPerfilEstudianteRequestMapper {

    private ConsultarEstadosFichaPerfilEstudianteRequestMapper() {}

    public static ConsultarEstadosFichaPerfilEstudianteQuery toQuery(UUID fichaPerfilId, UUID estudianteId) {
        return ConsultarEstadosFichaPerfilEstudianteQuery.crear(fichaPerfilId, estudianteId);
    }
}
