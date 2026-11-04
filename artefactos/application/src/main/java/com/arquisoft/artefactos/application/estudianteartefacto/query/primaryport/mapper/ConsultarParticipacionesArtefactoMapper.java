package com.arquisoft.artefactos.application.estudianteartefacto.query.primaryport.mapper;

import com.arquisoft.artefactos.application.estudianteartefacto.query.criteria.ParticipacionArtefactoCriteria;
import com.arquisoft.artefactos.application.estudianteartefacto.query.primaryport.model.ConsultarParticipacionesArtefactoQuery;

public final class ConsultarParticipacionesArtefactoMapper {

    private ConsultarParticipacionesArtefactoMapper() {}

    public static ParticipacionArtefactoCriteria toCriteria(ConsultarParticipacionesArtefactoQuery query) {
        return new ParticipacionArtefactoCriteria(query.estudiante());
    }
}
