package com.arquisoft.entregables.application.artefactoentregable.query.primaryport.mapper;

import com.arquisoft.entregables.application.artefactoentregable.query.criteria.ArtefactoEntregableCriteria;
import com.arquisoft.entregables.application.artefactoentregable.query.primaryport.model.ConsultarArtefactosEntregableQuery;

public final class ConsultarArtefactosEntregableMapper {

    private ConsultarArtefactosEntregableMapper() {}

    public static ArtefactoEntregableCriteria toCriteria(ConsultarArtefactosEntregableQuery query) {
        return new ArtefactoEntregableCriteria(query.entregableProyectoGrado());
    }
}
