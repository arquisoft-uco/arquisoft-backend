package com.arquisoft.entregables.application.artefacto.query.usecase;

import com.arquisoft.entregables.application.artefacto.query.criteria.VistaPreviaPaqueteCriteria;
import com.arquisoft.entregables.application.artefacto.query.readmodel.ArtefactoVistaPreviaReadModel;
import com.arquisoft.shared.usecase.UseCase;

import java.util.List;

public interface ConsultarVistaPreviaPaqueteUseCase
        extends UseCase<VistaPreviaPaqueteCriteria, List<ArtefactoVistaPreviaReadModel>> {
}
