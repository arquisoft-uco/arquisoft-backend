package com.arquisoft.artefactos.application.revisionasesor.query.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.revisionasesor.query.primaryport.interactor.ConsultarRevisionesAsesorAsesorInteractor;
import com.arquisoft.artefactos.application.revisionasesor.query.primaryport.mapper.ConsultarRevisionesAsesorAsesorMapper;
import com.arquisoft.artefactos.application.revisionasesor.query.primaryport.model.ConsultarRevisionesAsesorAsesorQuery;
import com.arquisoft.artefactos.application.revisionasesor.query.readmodel.RevisionAsesorAsesorReadModel;
import com.arquisoft.artefactos.application.revisionasesor.query.usecase.ConsultarRevisionesAsesorAsesorUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarRevisionesAsesorAsesorInteractorImpl implements ConsultarRevisionesAsesorAsesorInteractor {

    private final ConsultarRevisionesAsesorAsesorUseCase consultarRevisionesAsesorAsesorUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "artefactosTransactionManager")
    public PaginatedResult<RevisionAsesorAsesorReadModel> ejecutar(ConsultarRevisionesAsesorAsesorQuery entrada) {
        var criteria = ConsultarRevisionesAsesorAsesorMapper.toCriteria(entrada);
        return consultarRevisionesAsesorAsesorUseCase.ejecutar(criteria);
    }
}
