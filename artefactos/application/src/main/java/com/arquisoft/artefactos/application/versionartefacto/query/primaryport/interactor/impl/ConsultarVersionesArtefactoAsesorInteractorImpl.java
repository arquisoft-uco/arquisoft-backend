package com.arquisoft.artefactos.application.versionartefacto.query.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.versionartefacto.query.primaryport.interactor.ConsultarVersionesArtefactoAsesorInteractor;
import com.arquisoft.artefactos.application.versionartefacto.query.primaryport.mapper.ConsultarVersionesArtefactoAsesorMapper;
import com.arquisoft.artefactos.application.versionartefacto.query.primaryport.model.ConsultarVersionesArtefactoAsesorQuery;
import com.arquisoft.artefactos.application.versionartefacto.query.readmodel.VersionArtefactoReadModel;
import com.arquisoft.artefactos.application.versionartefacto.query.usecase.ConsultarVersionesArtefactoAsesorUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarVersionesArtefactoAsesorInteractorImpl implements ConsultarVersionesArtefactoAsesorInteractor {

    private final ConsultarVersionesArtefactoAsesorUseCase consultarVersionesArtefactoAsesorUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "artefactosTransactionManager")
    public PaginatedResult<VersionArtefactoReadModel> ejecutar(ConsultarVersionesArtefactoAsesorQuery entrada) {
        var criteria = ConsultarVersionesArtefactoAsesorMapper.toCriteria(entrada);
        return consultarVersionesArtefactoAsesorUseCase.ejecutar(criteria);
    }
}
