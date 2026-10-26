package com.arquisoft.artefactos.application.artefacto.query.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.artefacto.query.primaryport.interactor.ConsultarArtefactosAsesorInteractor;
import com.arquisoft.artefactos.application.artefacto.query.primaryport.mapper.ConsultarArtefactosAsesorMapper;
import com.arquisoft.artefactos.application.artefacto.query.primaryport.model.ConsultarArtefactosAsesorQuery;
import com.arquisoft.artefactos.application.artefacto.query.readmodel.ArtefactoReadModel;
import com.arquisoft.artefactos.application.artefacto.query.usecase.ConsultarArtefactosAsesorUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarArtefactosAsesorInteractorImpl implements ConsultarArtefactosAsesorInteractor {

    private final ConsultarArtefactosAsesorUseCase consultarArtefactosAsesorUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "artefactosTransactionManager")
    public PaginatedResult<ArtefactoReadModel> ejecutar(ConsultarArtefactosAsesorQuery entrada) {
        var criteria = ConsultarArtefactosAsesorMapper.toCriteria(entrada);
        return consultarArtefactosAsesorUseCase.ejecutar(criteria);
    }
}
