package com.arquisoft.fichas.application.observacionitem.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.observacionitem.query.primaryport.interactor.ConsultarObservacionesItemEstudianteInteractor;
import com.arquisoft.fichas.application.observacionitem.query.primaryport.mapper.ConsultarObservacionesItemEstudianteMapper;
import com.arquisoft.fichas.application.observacionitem.query.primaryport.model.ConsultarObservacionesItemEstudianteQuery;
import com.arquisoft.fichas.application.observacionitem.query.readmodel.ObservacionItemReadModel;
import com.arquisoft.fichas.application.observacionitem.query.usecase.ConsultarObservacionesItemEstudianteUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarObservacionesItemEstudianteInteractorImpl implements ConsultarObservacionesItemEstudianteInteractor {

    private final ConsultarObservacionesItemEstudianteUseCase consultarObservacionesItemEstudianteUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "fichasTransactionManager")
    public PaginatedResult<ObservacionItemReadModel> ejecutar(ConsultarObservacionesItemEstudianteQuery entrada) {
        var criteria = ConsultarObservacionesItemEstudianteMapper.toCriteria(entrada);
        return consultarObservacionesItemEstudianteUseCase.ejecutar(criteria);
    }
}
