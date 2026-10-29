package com.arquisoft.entregables.application.artefactoentregable.query.usecase;

import com.arquisoft.entregables.application.artefactoentregable.query.criteria.ArtefactoEntregableCriteria;
import com.arquisoft.entregables.application.artefactoentregable.query.readmodel.ArtefactoEntregableReadModel;
import com.arquisoft.shared.usecase.UseCase;

import java.util.List;

public interface ConsultarArtefactosEntregableUseCase
        extends UseCase<ArtefactoEntregableCriteria, List<ArtefactoEntregableReadModel>> {}
