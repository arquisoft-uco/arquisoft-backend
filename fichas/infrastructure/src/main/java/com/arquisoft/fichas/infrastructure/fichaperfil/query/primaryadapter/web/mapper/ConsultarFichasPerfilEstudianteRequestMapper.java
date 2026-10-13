package com.arquisoft.fichas.infrastructure.fichaperfil.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.fichaperfil.query.primaryport.model.ConsultarFichasPerfilEstudianteQuery;

import java.util.UUID;

public final class ConsultarFichasPerfilEstudianteRequestMapper {

    private ConsultarFichasPerfilEstudianteRequestMapper() {}

    public static ConsultarFichasPerfilEstudianteQuery toQuery(UUID estudiante) {
        return ConsultarFichasPerfilEstudianteQuery.crear(estudiante);
    }
}
