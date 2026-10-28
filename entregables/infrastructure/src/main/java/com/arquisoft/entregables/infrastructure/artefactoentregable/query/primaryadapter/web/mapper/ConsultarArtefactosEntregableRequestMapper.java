package com.arquisoft.entregables.infrastructure.artefactoentregable.query.primaryadapter.web.mapper;

import com.arquisoft.entregables.application.artefactoentregable.query.primaryport.model.ConsultarArtefactosEntregableQuery;

import java.util.UUID;

public final class ConsultarArtefactosEntregableRequestMapper {

    private ConsultarArtefactosEntregableRequestMapper() {}

    public static ConsultarArtefactosEntregableQuery toQuery(UUID entregableProyectoGrado) {
        return ConsultarArtefactosEntregableQuery.crear(entregableProyectoGrado);
    }
}
