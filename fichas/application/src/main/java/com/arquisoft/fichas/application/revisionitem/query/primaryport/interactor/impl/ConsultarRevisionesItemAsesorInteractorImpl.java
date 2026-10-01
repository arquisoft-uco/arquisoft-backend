package com.arquisoft.fichas.application.revisionitem.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.revisionitem.query.primaryport.interactor.ConsultarRevisionesItemAsesorInteractor;
import com.arquisoft.fichas.application.revisionitem.query.primaryport.mapper.ConsultarRevisionesItemAsesorMapper;
import com.arquisoft.fichas.application.revisionitem.query.primaryport.model.ConsultarRevisionesItemAsesorQuery;
import com.arquisoft.fichas.application.revisionitem.query.readmodel.RevisionItemReadModel;
import com.arquisoft.fichas.application.revisionitem.query.usecase.ConsultarRevisionesItemAsesorUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarRevisionesItemAsesorInteractorImpl implements ConsultarRevisionesItemAsesorInteractor {

    private final ConsultarRevisionesItemAsesorUseCase consultarRevisionesItemAsesorUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "fichasTransactionManager")
    public PaginatedResult<RevisionItemReadModel> ejecutar(ConsultarRevisionesItemAsesorQuery entrada) {
        var criteria = ConsultarRevisionesItemAsesorMapper.toCriteria(entrada);
        return consultarRevisionesItemAsesorUseCase.ejecutar(criteria);
    }
}
