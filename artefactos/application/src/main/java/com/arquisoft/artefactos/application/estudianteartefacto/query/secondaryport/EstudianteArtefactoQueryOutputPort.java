package com.arquisoft.artefactos.application.estudianteartefacto.query.secondaryport;

import com.arquisoft.artefactos.application.estudianteartefacto.query.criteria.ParticipacionArtefactoCriteria;
import com.arquisoft.artefactos.application.estudianteartefacto.query.readmodel.EstudianteArtefactoReadModel;
import com.arquisoft.artefactos.application.estudianteartefacto.query.readmodel.ParticipacionArtefactoReadModel;

import com.arquisoft.shared.query.pagination.PaginatedResult;

import java.util.List;
import java.util.UUID;

public interface EstudianteArtefactoQueryOutputPort {

    List<EstudianteArtefactoReadModel> consultarPorArtefacto(UUID artefacto);

    PaginatedResult<ParticipacionArtefactoReadModel> consultarParticipaciones(ParticipacionArtefactoCriteria criteria);
}
