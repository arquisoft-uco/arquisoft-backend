package com.arquisoft.fichas.infrastructure.estadofichaperfil.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.model.ConsultarEstadosFichaPerfilRepresentanteQuery;

import java.util.UUID;

public final class ConsultarEstadosFichaPerfilRepresentanteRequestMapper {

    private ConsultarEstadosFichaPerfilRepresentanteRequestMapper() {}

    public static ConsultarEstadosFichaPerfilRepresentanteQuery toQuery(UUID fichaPerfilId, UUID representanteComite) {
        return ConsultarEstadosFichaPerfilRepresentanteQuery.crear(fichaPerfilId, representanteComite);
    }
}
