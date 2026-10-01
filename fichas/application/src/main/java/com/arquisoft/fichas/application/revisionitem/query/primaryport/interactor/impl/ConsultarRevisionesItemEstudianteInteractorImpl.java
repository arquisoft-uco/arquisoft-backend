package com.arquisoft.fichas.application.revisionitem.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.revisionitem.query.primaryport.interactor.ConsultarRevisionesItemEstudianteInteractor;
import com.arquisoft.fichas.application.revisionitem.query.primaryport.mapper.ConsultarRevisionesItemEstudianteMapper;
import com.arquisoft.fichas.application.revisionitem.query.primaryport.model.ConsultarRevisionesItemEstudianteQuery;
import com.arquisoft.fichas.application.revisionitem.query.readmodel.RevisionItemReadModel;
import com.arquisoft.fichas.application.revisionitem.query.usecase.ConsultarRevisionesItemEstudianteUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarRevisionesItemEstudianteInteractorImpl implements ConsultarRevisionesItemEstudianteInteractor {

    private final ConsultarRevisionesItemEstudianteUseCase consultarRevisionesItemEstudianteUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "fichasTransactionManager")
    public PaginatedResult<RevisionItemReadModel> ejecutar(ConsultarRevisionesItemEstudianteQuery entrada) {
        var criteria = ConsultarRevisionesItemEstudianteMapper.toCriteria(entrada);
        return consultarRevisionesItemEstudianteUseCase.ejecutar(criteria);
    }
}
