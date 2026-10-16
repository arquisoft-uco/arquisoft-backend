package com.arquisoft.fichas.infrastructure.itemfichaperfil.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.model.ConsultarItemsFichaPerfilCoordinadorQuery;

import java.util.UUID;

public final class ConsultarItemsFichaPerfilCoordinadorRequestMapper {

    private ConsultarItemsFichaPerfilCoordinadorRequestMapper() {}

    public static ConsultarItemsFichaPerfilCoordinadorQuery toQuery(UUID fichaPerfilId) {
        return ConsultarItemsFichaPerfilCoordinadorQuery.crear(fichaPerfilId);
    }
}
