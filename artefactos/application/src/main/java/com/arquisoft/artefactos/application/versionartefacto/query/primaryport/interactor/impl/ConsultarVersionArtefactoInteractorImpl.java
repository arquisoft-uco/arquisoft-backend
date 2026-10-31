package com.arquisoft.artefactos.application.versionartefacto.query.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.versionartefacto.query.primaryport.interactor.ConsultarVersionArtefactoInteractor;
import com.arquisoft.artefactos.application.versionartefacto.query.primaryport.mapper.ConsultarVersionArtefactoMapper;
import com.arquisoft.artefactos.application.versionartefacto.query.primaryport.model.ConsultarVersionArtefactoQuery;
import com.arquisoft.artefactos.application.versionartefacto.query.readmodel.VersionArtefactoReadModel;
import com.arquisoft.artefactos.application.versionartefacto.query.usecase.ConsultarVersionArtefactoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarVersionArtefactoInteractorImpl implements ConsultarVersionArtefactoInteractor {

    private final ConsultarVersionArtefactoUseCase consultarVersionArtefactoUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "artefactosTransactionManager")
    public VersionArtefactoReadModel ejecutar(ConsultarVersionArtefactoQuery entrada) {
        var criteria = ConsultarVersionArtefactoMapper.toCriteria(entrada);
        return consultarVersionArtefactoUseCase.ejecutar(criteria);
    }
}
