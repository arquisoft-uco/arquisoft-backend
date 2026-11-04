package com.arquisoft.repositorio_artefactos.application.repositorioartefacto.query.primaryport.interactor.impl;

import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.query.primaryport.interactor.ConsultarRepositoriosArtefactoInteractor;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.query.primaryport.mapper.ConsultarRepositoriosArtefactoMapper;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.query.readmodel.RepositorioArtefactoReadModel;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.query.usecase.ConsultarRepositoriosArtefactoUseCase;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarRepositoriosArtefactoInteractorImpl implements ConsultarRepositoriosArtefactoInteractor {

    private final ConsultarRepositoriosArtefactoUseCase consultarRepositoriosArtefactoUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "repositorioArtefactosTransactionManager")
    public PaginatedResult<RepositorioArtefactoReadModel> ejecutar(ConsultaCriteriaQuery entrada) {
        var criteria = ConsultarRepositoriosArtefactoMapper.toCriteria(entrada);
        return consultarRepositoriosArtefactoUseCase.ejecutar(criteria);
    }
}
