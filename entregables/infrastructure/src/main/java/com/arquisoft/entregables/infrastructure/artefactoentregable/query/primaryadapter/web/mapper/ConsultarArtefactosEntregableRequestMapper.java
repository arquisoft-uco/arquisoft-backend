package com.arquisoft.entregables.infrastructure.artefactoentregable.query.primaryadapter.web.mapper;

import com.arquisoft.entregables.application.artefactoentregable.query.primaryport.model.ConsultarArtefactosEntregableQuery;

import com.arquisoft.shared.web.identidad.IdentidadActual;

import java.util.UUID;

public final class ConsultarArtefactosEntregableRequestMapper {

    private ConsultarArtefactosEntregableRequestMapper() {}

    public static ConsultarArtefactosEntregableQuery toQuery(UUID entregableProyectoGrado, IdentidadActual identidad) {
        return ConsultarArtefactosEntregableQuery.crear(entregableProyectoGrado, identidad.id());
    }
}
