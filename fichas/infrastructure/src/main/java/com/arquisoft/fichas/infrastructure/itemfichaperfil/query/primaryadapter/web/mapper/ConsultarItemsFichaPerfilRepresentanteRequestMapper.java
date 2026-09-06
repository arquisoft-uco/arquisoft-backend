package com.arquisoft.fichas.infrastructure.itemfichaperfil.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.model.ConsultarItemsFichaPerfilRepresentanteQuery;

import java.util.UUID;

public final class ConsultarItemsFichaPerfilRepresentanteRequestMapper {

    private ConsultarItemsFichaPerfilRepresentanteRequestMapper() {}

    public static ConsultarItemsFichaPerfilRepresentanteQuery toQuery(UUID fichaPerfilId, UUID representanteId) {
        return ConsultarItemsFichaPerfilRepresentanteQuery.crear(fichaPerfilId, representanteId);
    }
}
