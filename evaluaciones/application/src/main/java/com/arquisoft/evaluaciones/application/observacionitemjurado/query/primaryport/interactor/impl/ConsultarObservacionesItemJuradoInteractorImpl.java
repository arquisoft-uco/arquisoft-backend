package com.arquisoft.evaluaciones.application.observacionitemjurado.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.observacionitemjurado.query.primaryport.interactor.ConsultarObservacionesItemJuradoInteractor;
import com.arquisoft.evaluaciones.application.observacionitemjurado.query.primaryport.mapper.ConsultarObservacionesItemJuradoMapper;
import com.arquisoft.evaluaciones.application.observacionitemjurado.query.primaryport.model.ConsultarObservacionesItemJuradoQuery;
import com.arquisoft.evaluaciones.application.observacionitemjurado.query.readmodel.ObservacionItemJuradoReadModel;
import com.arquisoft.evaluaciones.application.observacionitemjurado.query.usecase.ConsultarObservacionesItemJuradoUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarObservacionesItemJuradoInteractorImpl implements ConsultarObservacionesItemJuradoInteractor {

    private final ConsultarObservacionesItemJuradoUseCase consultarObservacionesItemJuradoUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "evaluacionesTransactionManager")
    public PaginatedResult<ObservacionItemJuradoReadModel> ejecutar(ConsultarObservacionesItemJuradoQuery entrada) {
        var criteria = ConsultarObservacionesItemJuradoMapper.toCriteria(entrada);
        return consultarObservacionesItemJuradoUseCase.ejecutar(criteria);
    }
}
