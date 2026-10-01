package com.arquisoft.fichas.infrastructure.itemfichaperfil.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.model.ConsultarItemsFichaPerfilEstudianteQuery;

import java.util.UUID;

public final class ConsultarItemsFichaPerfilEstudianteRequestMapper {

    private ConsultarItemsFichaPerfilEstudianteRequestMapper() {}

    public static ConsultarItemsFichaPerfilEstudianteQuery toQuery(UUID fichaPerfilId, UUID estudianteId) {
        return ConsultarItemsFichaPerfilEstudianteQuery.crear(fichaPerfilId, estudianteId);
    }
}
