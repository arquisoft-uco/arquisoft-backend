package com.arquisoft.fichas.infrastructure.estudiantefichaperfil.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.estudiantefichaperfil.query.primaryport.model.ConsultarCompanerosFichaPerfilQuery;

import java.util.UUID;

public final class ConsultarCompanerosFichaPerfilRequestMapper {

    private ConsultarCompanerosFichaPerfilRequestMapper() {}

    public static ConsultarCompanerosFichaPerfilQuery toQuery(UUID fichaPerfilId, UUID estudianteId) {
        return ConsultarCompanerosFichaPerfilQuery.crear(fichaPerfilId, estudianteId);
    }
}
