package com.arquisoft.fichas.infrastructure.estudiantefichaperfil.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.estudiantefichaperfil.query.primaryport.model.ConsultarEstudiantesFichaPerfilQuery;

import java.util.UUID;

public final class ConsultarEstudiantesFichaPerfilRequestMapper {

    private ConsultarEstudiantesFichaPerfilRequestMapper() {}

    public static ConsultarEstudiantesFichaPerfilQuery toQuery(UUID fichaPerfilId) {
        return ConsultarEstudiantesFichaPerfilQuery.crear(fichaPerfilId);
    }
}
