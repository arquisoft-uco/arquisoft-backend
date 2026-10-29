package com.arquisoft.artefactos.application.estudianteartefacto.query.usecase;

import com.arquisoft.artefactos.application.estudianteartefacto.query.criteria.ParticipacionArtefactoCriteria;
import com.arquisoft.artefactos.application.estudianteartefacto.query.readmodel.ParticipacionArtefactoReadModel;
import com.arquisoft.shared.usecase.UseCase;

import java.util.List;

public interface ConsultarParticipacionesArtefactoUseCase
        extends UseCase<ParticipacionArtefactoCriteria, List<ParticipacionArtefactoReadModel>> {}
