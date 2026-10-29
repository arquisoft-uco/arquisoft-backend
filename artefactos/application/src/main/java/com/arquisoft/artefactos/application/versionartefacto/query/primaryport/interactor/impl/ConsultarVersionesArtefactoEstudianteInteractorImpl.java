package com.arquisoft.artefactos.application.versionartefacto.query.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.versionartefacto.query.primaryport.interactor.ConsultarVersionesArtefactoEstudianteInteractor;
import com.arquisoft.artefactos.application.versionartefacto.query.primaryport.mapper.ConsultarVersionesArtefactoEstudianteMapper;
import com.arquisoft.artefactos.application.versionartefacto.query.primaryport.model.ConsultarVersionesArtefactoEstudianteQuery;
import com.arquisoft.artefactos.application.versionartefacto.query.readmodel.VersionArtefactoReadModel;
import com.arquisoft.artefactos.application.versionartefacto.query.usecase.ConsultarVersionesArtefactoEstudianteUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarVersionesArtefactoEstudianteInteractorImpl implements ConsultarVersionesArtefactoEstudianteInteractor {

    private final ConsultarVersionesArtefactoEstudianteUseCase consultarVersionesArtefactoEstudianteUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "artefactosTransactionManager")
    public PaginatedResult<VersionArtefactoReadModel> ejecutar(ConsultarVersionesArtefactoEstudianteQuery entrada) {
        var criteria = ConsultarVersionesArtefactoEstudianteMapper.toCriteria(entrada);
        return consultarVersionesArtefactoEstudianteUseCase.ejecutar(criteria);
    }
}
