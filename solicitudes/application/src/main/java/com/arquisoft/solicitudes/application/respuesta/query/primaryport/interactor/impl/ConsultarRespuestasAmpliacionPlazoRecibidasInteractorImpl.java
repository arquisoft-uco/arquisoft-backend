package com.arquisoft.solicitudes.application.respuesta.query.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.respuesta.query.primaryport.interactor.ConsultarRespuestasAmpliacionPlazoRecibidasInteractor;
import com.arquisoft.solicitudes.application.respuesta.query.primaryport.mapper.ConsultarRespuestasAmpliacionPlazoRecibidasMapper;
import com.arquisoft.solicitudes.application.respuesta.query.primaryport.model.ConsultarRespuestasAmpliacionPlazoRecibidasQuery;
import com.arquisoft.solicitudes.application.respuesta.query.readmodel.RespuestaReadModel;
import com.arquisoft.solicitudes.application.respuesta.query.usecase.ConsultarRespuestasUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarRespuestasAmpliacionPlazoRecibidasInteractorImpl
        implements ConsultarRespuestasAmpliacionPlazoRecibidasInteractor {

    private final ConsultarRespuestasUseCase consultarRespuestasUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "solicitudesTransactionManager")
    public PaginatedResult<RespuestaReadModel> ejecutar(
            ConsultarRespuestasAmpliacionPlazoRecibidasQuery input) {
        var criteria = ConsultarRespuestasAmpliacionPlazoRecibidasMapper.toCriteria(input);
        return consultarRespuestasUseCase.ejecutar(criteria);
    }
}
