package com.arquisoft.fichas.infrastructure.estadofichaperfil.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.model.ConsultarEstadosFichaPerfilCoordinadorQuery;

import java.util.UUID;

public final class ConsultarEstadosFichaPerfilCoordinadorRequestMapper {

    private ConsultarEstadosFichaPerfilCoordinadorRequestMapper() {}

    public static ConsultarEstadosFichaPerfilCoordinadorQuery toQuery(UUID fichaPerfilId) {
        return ConsultarEstadosFichaPerfilCoordinadorQuery.crear(fichaPerfilId);
    }
}
