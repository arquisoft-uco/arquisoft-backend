package com.arquisoft.artefactos.infrastructure.estudianteartefacto.query.primaryadapter.web.mapper;

import com.arquisoft.artefactos.application.estudianteartefacto.query.primaryport.model.ConsultarParticipacionesArtefactoQuery;

import java.util.UUID;

public final class ConsultarParticipacionesArtefactoRequestMapper {

    private ConsultarParticipacionesArtefactoRequestMapper() {}

    public static ConsultarParticipacionesArtefactoQuery toQuery(UUID estudiante) {
        return ConsultarParticipacionesArtefactoQuery.crear(estudiante);
    }
}
