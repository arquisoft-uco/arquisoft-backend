package com.arquisoft.entregables.application.artefactoentregable.query.primaryport.interactor.impl;

import com.arquisoft.entregables.application.artefactoentregable.query.primaryport.interactor.ConsultarArtefactosEntregableInteractor;
import com.arquisoft.entregables.application.artefactoentregable.query.primaryport.mapper.ConsultarArtefactosEntregableMapper;
import com.arquisoft.entregables.application.artefactoentregable.query.primaryport.model.ConsultarArtefactosEntregableQuery;
import com.arquisoft.entregables.application.artefactoentregable.query.readmodel.ArtefactoEntregableReadModel;
import com.arquisoft.entregables.application.artefactoentregable.query.usecase.ConsultarArtefactosEntregableUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarArtefactosEntregableInteractorImpl implements ConsultarArtefactosEntregableInteractor {

    private final ConsultarArtefactosEntregableUseCase consultarArtefactosEntregableUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "entregablesTransactionManager")
    public List<ArtefactoEntregableReadModel> ejecutar(ConsultarArtefactosEntregableQuery entrada) {
        var criteria = ConsultarArtefactosEntregableMapper.toCriteria(entrada);
        return consultarArtefactosEntregableUseCase.ejecutar(criteria);
    }
}
