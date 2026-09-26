package com.arquisoft.fichas.application.observacionitem.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.observacionitem.query.primaryport.interactor.ConsultarObservacionesItemAsesorInteractor;
import com.arquisoft.fichas.application.observacionitem.query.primaryport.mapper.ConsultarObservacionesItemAsesorMapper;
import com.arquisoft.fichas.application.observacionitem.query.primaryport.model.ConsultarObservacionesItemAsesorQuery;
import com.arquisoft.fichas.application.observacionitem.query.readmodel.ObservacionItemReadModel;
import com.arquisoft.fichas.application.observacionitem.query.usecase.ConsultarObservacionesItemAsesorUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarObservacionesItemAsesorInteractorImpl implements ConsultarObservacionesItemAsesorInteractor {

    private final ConsultarObservacionesItemAsesorUseCase consultarObservacionesItemAsesorUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "fichasTransactionManager")
    public PaginatedResult<ObservacionItemReadModel> ejecutar(ConsultarObservacionesItemAsesorQuery entrada) {
        var criteria = ConsultarObservacionesItemAsesorMapper.toCriteria(entrada);
        return consultarObservacionesItemAsesorUseCase.ejecutar(criteria);
    }
}
