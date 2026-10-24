package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.primaryport.interactor.impl;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.primaryport.interactor.ConsultarVersionesRepositorioArtefactoAsesorInteractor;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.primaryport.mapper.ConsultarVersionesRepositorioArtefactoAsesorMapper;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.readmodel.VersionRepositorioArtefactoAsesorReadModel;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.usecase.ConsultarVersionesRepositorioArtefactoAsesorUseCase;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarVersionesRepositorioArtefactoAsesorInteractorImpl
        implements ConsultarVersionesRepositorioArtefactoAsesorInteractor {

    private final ConsultarVersionesRepositorioArtefactoAsesorUseCase consultarVersionesAsesorUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "repositorioArtefactosTransactionManager")
    public PaginatedResult<VersionRepositorioArtefactoAsesorReadModel> ejecutar(ConsultaCriteriaQuery entrada) {
        var criteria = ConsultarVersionesRepositorioArtefactoAsesorMapper.toCriteria(entrada);
        return consultarVersionesAsesorUseCase.ejecutar(criteria);
    }
}
