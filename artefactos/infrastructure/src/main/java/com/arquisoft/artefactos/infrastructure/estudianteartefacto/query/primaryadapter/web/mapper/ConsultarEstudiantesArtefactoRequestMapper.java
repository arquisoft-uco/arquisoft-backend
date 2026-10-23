package com.arquisoft.artefactos.infrastructure.estudianteartefacto.query.primaryadapter.web.mapper;

import com.arquisoft.artefactos.application.estudianteartefacto.query.primaryport.model.ConsultarEstudiantesArtefactoQuery;

import java.util.UUID;

public final class ConsultarEstudiantesArtefactoRequestMapper {

    private ConsultarEstudiantesArtefactoRequestMapper() {}

    public static ConsultarEstudiantesArtefactoQuery toQuery(UUID artefactoId) {
        return ConsultarEstudiantesArtefactoQuery.crear(artefactoId);
    }
}
