package com.arquisoft.fichas.infrastructure.fichaperfil.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.fichaperfil.query.primaryport.model.ConsultarFichaPerfilEstudianteQuery;

import java.util.UUID;

public final class ConsultarFichaPerfilEstudianteRequestMapper {

    private ConsultarFichaPerfilEstudianteRequestMapper() {}

    public static ConsultarFichaPerfilEstudianteQuery toQuery(UUID fichaPerfilId, UUID estudianteId) {
        return ConsultarFichaPerfilEstudianteQuery.crear(fichaPerfilId, estudianteId);
    }
}
