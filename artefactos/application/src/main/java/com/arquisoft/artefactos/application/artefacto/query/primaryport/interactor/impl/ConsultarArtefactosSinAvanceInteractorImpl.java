package com.arquisoft.artefactos.application.artefacto.query.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.artefacto.query.primaryport.interactor.ConsultarArtefactosSinAvanceInteractor;
import com.arquisoft.artefactos.application.artefacto.query.primaryport.mapper.ConsultarArtefactosSinAvanceMapper;
import com.arquisoft.artefactos.application.artefacto.query.primaryport.model.ConsultarArtefactosSinAvanceQuery;
import com.arquisoft.artefactos.application.artefacto.query.readmodel.ArtefactoSinAvanceReadModel;
import com.arquisoft.artefactos.application.artefacto.query.usecase.ConsultarArtefactosSinAvanceUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarArtefactosSinAvanceInteractorImpl implements ConsultarArtefactosSinAvanceInteractor {

    private final ConsultarArtefactosSinAvanceUseCase consultarArtefactosSinAvanceUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "artefactosTransactionManager")
    public PaginatedResult<ArtefactoSinAvanceReadModel> ejecutar(ConsultarArtefactosSinAvanceQuery entrada) {
        var criteria = ConsultarArtefactosSinAvanceMapper.toCriteria(entrada);
        return consultarArtefactosSinAvanceUseCase.ejecutar(criteria);
    }
}
