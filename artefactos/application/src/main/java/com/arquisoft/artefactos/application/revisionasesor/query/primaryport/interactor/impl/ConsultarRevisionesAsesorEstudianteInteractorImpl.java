package com.arquisoft.artefactos.application.revisionasesor.query.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.revisionasesor.query.primaryport.interactor.ConsultarRevisionesAsesorEstudianteInteractor;
import com.arquisoft.artefactos.application.revisionasesor.query.primaryport.mapper.ConsultarRevisionesAsesorEstudianteMapper;
import com.arquisoft.artefactos.application.revisionasesor.query.primaryport.model.ConsultarRevisionesAsesorEstudianteQuery;
import com.arquisoft.artefactos.application.revisionasesor.query.readmodel.RevisionAsesorReadModel;
import com.arquisoft.artefactos.application.revisionasesor.query.usecase.ConsultarRevisionesAsesorEstudianteUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarRevisionesAsesorEstudianteInteractorImpl implements ConsultarRevisionesAsesorEstudianteInteractor {

    private final ConsultarRevisionesAsesorEstudianteUseCase consultarRevisionesAsesorEstudianteUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "artefactosTransactionManager")
    public PaginatedResult<RevisionAsesorReadModel> ejecutar(ConsultarRevisionesAsesorEstudianteQuery entrada) {
        var criteria = ConsultarRevisionesAsesorEstudianteMapper.toCriteria(entrada);
        return consultarRevisionesAsesorEstudianteUseCase.ejecutar(criteria);
    }
}
