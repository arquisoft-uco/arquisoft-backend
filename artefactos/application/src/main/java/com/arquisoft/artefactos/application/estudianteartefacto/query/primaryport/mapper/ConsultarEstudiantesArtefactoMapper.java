package com.arquisoft.artefactos.application.estudianteartefacto.query.primaryport.mapper;

import com.arquisoft.artefactos.application.estudianteartefacto.query.criteria.EstudianteArtefactoCriteria;
import com.arquisoft.artefactos.application.estudianteartefacto.query.primaryport.model.ConsultarEstudiantesArtefactoQuery;

public final class ConsultarEstudiantesArtefactoMapper {

    private ConsultarEstudiantesArtefactoMapper() {}

    public static EstudianteArtefactoCriteria toCriteria(ConsultarEstudiantesArtefactoQuery query) {
        return new EstudianteArtefactoCriteria(query.artefacto());
    }
}
