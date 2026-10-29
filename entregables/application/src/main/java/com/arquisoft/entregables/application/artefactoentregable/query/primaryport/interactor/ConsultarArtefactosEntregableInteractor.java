package com.arquisoft.entregables.application.artefactoentregable.query.primaryport.interactor;

import com.arquisoft.entregables.application.artefactoentregable.query.primaryport.model.ConsultarArtefactosEntregableQuery;
import com.arquisoft.entregables.application.artefactoentregable.query.readmodel.ArtefactoEntregableReadModel;
import com.arquisoft.shared.interactor.Interactor;

import java.util.List;

public interface ConsultarArtefactosEntregableInteractor
        extends Interactor<ConsultarArtefactosEntregableQuery, List<ArtefactoEntregableReadModel>> {}
