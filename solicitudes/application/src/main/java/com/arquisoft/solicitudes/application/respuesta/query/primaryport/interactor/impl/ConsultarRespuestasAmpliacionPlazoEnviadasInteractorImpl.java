package com.arquisoft.solicitudes.application.respuesta.query.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.respuesta.query.primaryport.interactor.ConsultarRespuestasAmpliacionPlazoEnviadasInteractor;
import com.arquisoft.solicitudes.application.respuesta.query.primaryport.mapper.ConsultarRespuestasAmpliacionPlazoEnviadasMapper;
import com.arquisoft.solicitudes.application.respuesta.query.primaryport.model.ConsultarRespuestasAmpliacionPlazoEnviadasQuery;
import com.arquisoft.solicitudes.application.respuesta.query.readmodel.RespuestaReadModel;
import com.arquisoft.solicitudes.application.respuesta.query.usecase.ConsultarRespuestasUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarRespuestasAmpliacionPlazoEnviadasInteractorImpl
        implements ConsultarRespuestasAmpliacionPlazoEnviadasInteractor {

    private final ConsultarRespuestasUseCase consultarRespuestasUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "solicitudesTransactionManager")
    public PaginatedResult<RespuestaReadModel> ejecutar(
            ConsultarRespuestasAmpliacionPlazoEnviadasQuery input) {
        var criteria = ConsultarRespuestasAmpliacionPlazoEnviadasMapper.toCriteria(input);
        return consultarRespuestasUseCase.ejecutar(criteria);
    }
}
