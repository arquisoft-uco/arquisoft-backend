package com.arquisoft.fichas.infrastructure.itemfichaperfil.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.model.ConsultarItemsFichaPerfilAsesorQuery;

import java.util.UUID;

public final class ConsultarItemsFichaPerfilAsesorRequestMapper {

    private ConsultarItemsFichaPerfilAsesorRequestMapper() {}

    public static ConsultarItemsFichaPerfilAsesorQuery toQuery(UUID fichaPerfilId, UUID asesorFicha) {
        return ConsultarItemsFichaPerfilAsesorQuery.crear(fichaPerfilId, asesorFicha);
    }
}
