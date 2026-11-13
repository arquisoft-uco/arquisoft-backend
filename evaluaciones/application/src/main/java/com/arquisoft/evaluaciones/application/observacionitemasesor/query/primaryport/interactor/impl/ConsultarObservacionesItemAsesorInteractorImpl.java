package com.arquisoft.evaluaciones.application.observacionitemasesor.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.observacionitemasesor.query.primaryport.interactor.ConsultarObservacionesItemAsesorInteractor;
import com.arquisoft.evaluaciones.application.observacionitemasesor.query.primaryport.mapper.ConsultarObservacionesItemAsesorMapper;
import com.arquisoft.evaluaciones.application.observacionitemasesor.query.primaryport.model.ConsultarObservacionesItemAsesorQuery;
import com.arquisoft.evaluaciones.application.observacionitemasesor.query.readmodel.ObservacionItemAsesorReadModel;
import com.arquisoft.evaluaciones.application.observacionitemasesor.query.usecase.ConsultarObservacionesItemAsesorUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarObservacionesItemAsesorInteractorImpl implements ConsultarObservacionesItemAsesorInteractor {

    private final ConsultarObservacionesItemAsesorUseCase consultarObservacionesItemAsesorUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "evaluacionesTransactionManager")
    public PaginatedResult<ObservacionItemAsesorReadModel> ejecutar(ConsultarObservacionesItemAsesorQuery entrada) {
        var criteria = ConsultarObservacionesItemAsesorMapper.toCriteria(entrada);
        return consultarObservacionesItemAsesorUseCase.ejecutar(criteria);
    }
}
