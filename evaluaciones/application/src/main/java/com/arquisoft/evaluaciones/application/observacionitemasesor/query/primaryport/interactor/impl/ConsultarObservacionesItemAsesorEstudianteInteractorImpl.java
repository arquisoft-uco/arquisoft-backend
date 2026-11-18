package com.arquisoft.evaluaciones.application.observacionitemasesor.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.observacionitemasesor.query.primaryport.interactor.ConsultarObservacionesItemAsesorEstudianteInteractor;
import com.arquisoft.evaluaciones.application.observacionitemasesor.query.primaryport.mapper.ConsultarObservacionesItemAsesorEstudianteMapper;
import com.arquisoft.evaluaciones.application.observacionitemasesor.query.primaryport.model.ConsultarObservacionesItemAsesorEstudianteQuery;
import com.arquisoft.evaluaciones.application.observacionitemasesor.query.readmodel.ObservacionItemAsesorReadModel;
import com.arquisoft.evaluaciones.application.observacionitemasesor.query.usecase.ConsultarObservacionesItemAsesorEstudianteUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarObservacionesItemAsesorEstudianteInteractorImpl
        implements ConsultarObservacionesItemAsesorEstudianteInteractor {

    private final ConsultarObservacionesItemAsesorEstudianteUseCase consultarObservacionesItemAsesorEstudianteUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "evaluacionesTransactionManager")
    public PaginatedResult<ObservacionItemAsesorReadModel> ejecutar(
            ConsultarObservacionesItemAsesorEstudianteQuery entrada) {
        var criteria = ConsultarObservacionesItemAsesorEstudianteMapper.toCriteria(entrada);
        return consultarObservacionesItemAsesorEstudianteUseCase.ejecutar(criteria);
    }
}
