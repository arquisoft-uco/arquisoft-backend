package com.arquisoft.artefactos.application.estudianteartefacto.query.usecase;

import com.arquisoft.artefactos.application.estudianteartefacto.query.criteria.EstudianteArtefactoCriteria;
import com.arquisoft.artefactos.application.estudianteartefacto.query.readmodel.EstudianteArtefactoReadModel;
import com.arquisoft.shared.usecase.UseCase;

import java.util.List;

public interface ConsultarEstudiantesArtefactoUseCase
        extends UseCase<EstudianteArtefactoCriteria, List<EstudianteArtefactoReadModel>> {}
